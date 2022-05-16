package org.jetbrains.topvisplugin.actions

import org.jetbrains.topvisplugin.settings.TopvisPluginSettings
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.components.service

class TopicsEnableAction : ToggleAction() {

    override fun isSelected(e: AnActionEvent): Boolean {
        val settings = e.project?.service<TopvisPluginSettings>()
        return settings?.topicsEnabled ?: false
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val settings = e.project?.service<TopvisPluginSettings>()
        val updated = state != isSelected(e)

        settings?.topicsEnabled = state

        if (updated) {
            e.project?.let { project ->
                val view = ProjectView.getInstance(project)
                view.currentProjectViewPane?.updateFromRoot(true)
            }
        }
    }
}