package org.jetbrains.topvisplugin.decorators

import org.jetbrains.topvisplugin.settings.TopvisPluginSettings
import org.jetbrains.topvisplugin.util.TopicsParser
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.components.service
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer


class TopicsDecorator : ProjectViewNodeDecorator {
    override fun decorate(node: ProjectViewNode<*>?, data: PresentationData?) {
        if (node != null) {
            node.virtualFile?.let { virtualFile ->
                val projectPath = node.project?.basePath
                val enabled = node.project?.service<TopvisPluginSettings>()?.topicsEnabled ?: false
                if (!enabled && data != null) {
                    data.locationString = null
                    return
                }
                val topicsInfo = projectPath?.let { virtualFile.path.removePrefix(it) }
                    ?.let { TopicsParser.getTopicsInfo(it) }
                if (topicsInfo != null && data != null) {
                    data.locationString = topicsInfo
                }
            }
        }
    }

    override fun decorate(node: PackageDependenciesNode?, cellRenderer: ColoredTreeCellRenderer?) = Unit
}