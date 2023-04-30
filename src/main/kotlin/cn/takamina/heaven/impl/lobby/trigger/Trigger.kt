package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import taboolib.platform.BukkitPlugin

abstract class Trigger(
    val group: Group,
    val args: Map<String, Any>
) : Listener {
    abstract fun trigger(args: MutableMap<String, Any?>)

    fun enable() {
        Bukkit.getPluginManager().registerEvents(this, BukkitPlugin.getInstance())
    }

    fun disable() {
        HandlerList.unregisterAll(this)
    }

    interface TriggerCreator {
        val id: String
        val create: (group: Group, args: Map<String, Any>, task: Task) -> Trigger
    }
}