package cn.takamina.heaven

import cn.takamina.heaven.impl.data.Lobbys
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.sendInfo

object Heaven : Plugin() {
    override fun onEnable() {
        console().sendInfo("Plugin-Enable", pluginVersion)
    }

    override fun onDisable() {
        Lobbys.unload()
    }
}