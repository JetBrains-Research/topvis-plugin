package com.github.salkaevruslan.topvisplugin.activities

import com.github.salkaevruslan.topvisplugin.util.TopicsParser
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity


class TopvisStartupActivity : StartupActivity {
    override fun runActivity(project: Project) {
        TopicsParser.parse(project,"topics.json")
    }
}