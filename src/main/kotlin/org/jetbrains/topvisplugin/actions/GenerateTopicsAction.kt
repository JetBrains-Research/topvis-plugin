package org.jetbrains.topvisplugin.actions

import org.jetbrains.topvisplugin.util.TopicsParser
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


abstract class GenerateTopicsAction : AnAction() {
    companion object {
        const val TOPICS_FILENAME = "topics.json"
    }

    abstract fun getMethod(): String

    override fun actionPerformed(e: AnActionEvent) {
        val task = object : Task.Backgroundable(e.project, "Generating topics", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.text = "Generating topics"
                indicator.isIndeterminate = false
                indicator.fraction = 0.0
                if (Files.isRegularFile(Path.of("${project.basePath}/$TOPICS_FILENAME"))) {
                    File("${project.basePath}/$TOPICS_FILENAME").delete()
                }
                val resource = javaClass.classLoader.getResourceAsStream("/topics.sh")
                val file = File("tmp.sh")
                file.createNewFile()
                Files.copy(resource, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                file.setExecutable(true)
                val scriptProcess = Runtime.getRuntime().exec("${file.absolutePath} ${getMethod()} ${project.basePath}")
                scriptProcess.waitFor()
                file.delete()
                if (Files.isRegularFile(Path.of(TOPICS_FILENAME))) {
                    Files.move(
                        Paths.get(TOPICS_FILENAME),
                        Paths.get("${project.basePath}/$TOPICS_FILENAME"),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                    File(TOPICS_FILENAME).delete()
                    TopicsParser.parse(project, TOPICS_FILENAME)
                }
                indicator.fraction = 1.0
                project?.let { project ->
                    val view = ProjectView.getInstance(project)
                    view.currentProjectViewPane?.updateFromRoot(true)
                }
            }
        }
        ProgressManager.getInstance().run(task)
    }
}
