package cn.takamina.heaven.impl.lobby

import cn.takamina.heaven.impl.event.GroupLoadEvent
import cn.takamina.heaven.impl.task.ScriptTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import taboolib.platform.BukkitPlugin

class Lobby(val id: String) {
    companion object {
        const val OUTSIDE = "_OUTSIDE_"
    }

    val groups = HashMap<String, Group>()

    val values = HashMap<String, Any?>()

    val scriptTasks = HashMap<String, ScriptTask>()

    fun enable() {
        groups.values.forEach { group ->
            Bukkit.getPluginManager().registerEvents(group, BukkitPlugin.getInstance())
            group.triggers.values.forEach { trigger ->
                trigger.enable()
            }
        }
        scriptTasks.values.forEach {
            it.call(hashMapOf())
        }
        groups.values.forEach { GroupLoadEvent(it).call() }
    }

    fun disable() {
        groups.values.forEach { group ->
            group.players.values.forEach { leave(it) }
        }
        groups.values.forEach { group ->
            group.triggers.values.forEach { trigger ->
                HandlerList.unregisterAll(trigger)
            }
            HandlerList.unregisterAll(group)
        }
    }

    fun containPlayer(player: Player): Boolean {
        return groups.values.filter { it.players.containsKey(player.name) }.isNotEmpty()
    }

    fun join(player: Player, group: String = "Main"): Boolean {
        return groups[group]?.let {
            var from = OUTSIDE
            groups.filter { entry ->
                entry.value.players.containsKey(player.name) && entry.key != group
            }.forEach { (_, g) ->
                from = g.id
                g.leave(player, group)
            }
            it.join(player, from)
            true
        } ?: false
    }

    fun leave(player: Player): Boolean {
        var contain = false
        groups.filter {
            it.value.players.containsKey(player.name)
        }.forEach { (_, group) ->
            group.leave(player, OUTSIDE)
            contain = true
        }
        return contain
    }
}