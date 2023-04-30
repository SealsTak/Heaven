package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import taboolib.platform.util.killer

class TriggerDeath(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        if (group.players.containsKey(player.name)) {
            trigger(
                mutableMapOf(
                    "player" to player.name,
                    "killer" to (event.killer?.name ?: ""),
                )
            )
        }
    }

    companion object : TriggerCreator {
        override val id = "Death"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerDeath =
            { group, args, task -> TriggerDeath(group, args, task) }
    }
}