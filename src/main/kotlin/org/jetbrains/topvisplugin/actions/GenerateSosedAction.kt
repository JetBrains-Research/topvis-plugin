package org.jetbrains.topvisplugin.actions

class GenerateSosedAction : GenerateTopicsAction() {
    override fun getMethod(): String {
        return "sosed"
    }
}