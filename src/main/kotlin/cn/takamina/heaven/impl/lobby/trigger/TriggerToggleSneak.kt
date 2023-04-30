package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent

class TriggerToggleSneak(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerCommandSend(event: PlayerToggleSneakEvent) {
        if (group.players.containsKey(event.player.name)) {
            if (args["Toggle"] == null || args["Toggle"] as Boolean == event.isSneaking) {
                trigger(
                    mutableMapOf(
                        "player" to event.player.name,
                        "sneaking" to event.isSneaking,
                    )
                )
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "ToggleSneak"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerToggleSneak =
            { group, args, task -> TriggerToggleSneak(group, args, task) }
    }
}