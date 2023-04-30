package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent

class TriggerKillPlayer(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity.killer
        if (player is Player && group.players.containsKey(player.name)) {
            trigger(
                mutableMapOf(
                    "player" to player.name,
                    "name" to event.entity.name,
                )
            )
        }
    }

    companion object : TriggerCreator {
        override val id = "KillPlayer"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerKillPlayer =
            { group, args, task -> TriggerKillPlayer(group, args, task) }
    }
}