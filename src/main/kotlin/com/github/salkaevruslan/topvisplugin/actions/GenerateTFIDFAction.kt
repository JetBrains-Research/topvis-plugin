package com.github.salkaevruslan.topvisplugin.actions

class GenerateTFIDFAction: GenerateTopicsAction() {
    override fun getMethod(): String {
        return "tfidf"
    }
}