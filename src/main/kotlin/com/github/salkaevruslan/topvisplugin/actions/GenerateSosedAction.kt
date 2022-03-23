package com.github.salkaevruslan.topvisplugin.actions

class GenerateSosedAction : GenerateTopicsAction() {
    override fun getMethod(): String {
        return "sosed"
    }
}