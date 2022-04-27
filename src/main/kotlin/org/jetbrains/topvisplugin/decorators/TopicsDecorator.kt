package org.jetbrains.topvisplugin.decorators

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.util.treeView.NodeRenderer.getSimpleTextAttributes
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.components.service
import com.intellij.openapi.util.text.StringUtil
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.UIUtil
import org.jetbrains.topvisplugin.settings.TopvisPluginSettings
import org.jetbrains.topvisplugin.util.TopicsParser


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
                    if (data.coloredText.isEmpty()) {
                        val text = data.presentableText
                        val attributes = getSimpleTextAttributes(data)
                        data.addText(PresentableNodeDescriptor.ColoredFragment(text, attributes))
                        val location: String? = data.locationString
                        if (!StringUtil.isEmpty(location)) {
                            val attributesWithLocation =
                                SimpleTextAttributes.merge(attributes, SimpleTextAttributes.GRAYED_ATTRIBUTES)
                            data.addText(
                                PresentableNodeDescriptor.ColoredFragment(
                                    data.locationPrefix + location + data.locationSuffix,
                                    attributesWithLocation
                                )
                            )
                        }
                    }
                    data.addText(
                        PresentableNodeDescriptor.ColoredFragment(
                            " $topicsInfo",
                            SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, UIUtil.getInactiveTextColor())
                        )
                    )
                    // data.locationString = topicsInfo
                }
            }
        }
    }

    override fun decorate(node: PackageDependenciesNode?, cellRenderer: ColoredTreeCellRenderer?) = Unit
}