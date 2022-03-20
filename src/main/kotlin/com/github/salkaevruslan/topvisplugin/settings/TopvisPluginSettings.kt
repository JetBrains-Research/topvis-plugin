package com.github.salkaevruslan.topvisplugin.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.OptionTag


@State(name = "TopvisPluginSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class TopvisPluginSettings : BaseState(), PersistentStateComponent<TopvisPluginSettings> {

    @get:OptionTag("TOPICS_ENABLED")
    var topicsEnabled by property(true)


    override fun getState(): TopvisPluginSettings = this

    override fun loadState(state: TopvisPluginSettings) = copyFrom(state)
}