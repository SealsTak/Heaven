package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import cn.takamina.heaven.utils.Area
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class TriggerLeaveArea(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (group.players.containsKey(player.name)) {
            event.to?.let { to ->
                val worlds = args["World"] as List<String>?
                if (worlds.isNullOrEmpty() || worlds.contains(to.world?.name)) {
                    val areaTexts = args["Area"] as List<String>?
                    if (areaTexts.isNullOrEmpty()) {
                        val areas = areaTexts?.map { Area.parse(it) }
                        areas?.filter {
                            event.from.toVector() in it && to.toVector() !in it
                        }?.forEach {
                            trigger(
                                mutableMapOf(
                                    "player" to player.name,
                                    "area" to areas.indexOf(it),
                                    "world" to (to.world?.name ?: ""),
                                    "x" to to.x,
                                    "y" to to.y,
                                    "z" to to.z,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (group.players.containsKey(player.name)) {
            event.to?.let { to ->
                val worlds = args["World"] as List<String>?
                if (worlds.isNullOrEmpty() || worlds.contains(to.world?.name)) {
                    val areaTexts = args["Area"] as List<String>?
                    if (areaTexts.isNullOrEmpty()) {
                        val areas = areaTexts?.map { Area.parse(it) }
                        areas?.filter {
                            event.from.toVector() in it && to.toVector() !in it
                        }?.forEach {
                            trigger(
                                mutableMapOf(
                                    "player" to player.name,
                                    "area" to areas.indexOf(it),
                                    "world" to (to.world?.name ?: ""),
                                    "x" to to.x,
                                    "y" to to.y,
                                    "z" to to.z,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "LeaveArea"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerLeaveArea =
            { group, args, task -> TriggerLeaveArea(group, args, task) }
    }
}