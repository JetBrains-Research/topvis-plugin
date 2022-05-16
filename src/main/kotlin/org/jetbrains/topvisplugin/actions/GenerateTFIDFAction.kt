package org.jetbrains.topvisplugin.actions

class GenerateTFIDFAction: GenerateTopicsAction() {
    override fun getMethod(): String {
        return "tfidf"
    }
}