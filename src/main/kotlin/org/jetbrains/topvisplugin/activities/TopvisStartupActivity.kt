package org.jetbrains.topvisplugin.activities

import org.jetbrains.topvisplugin.util.TopicsParser
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class TopvisStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        TopicsParser.parse(project,"topics.json")
    }
}