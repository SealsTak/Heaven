package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

class TriggerKillEntity(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val player = event.entity.killer
        if (player is Player && group.players.containsKey(player.name) && event.entity !is Player) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.entity.type.name)) {
                val names = args["Name"] as List<String>?
                if (names.isNullOrEmpty() || names.contains(event.entity.name)) {
                    trigger(
                        mutableMapOf(
                            "player" to player.name,
                            "name" to event.entity.name,
                            "type" to event.entity.type.name,
                        )
                    )
                }
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "KillEntity"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerKillEntity =
            { group, args, task -> TriggerKillEntity(group, args, task) }
    }
}