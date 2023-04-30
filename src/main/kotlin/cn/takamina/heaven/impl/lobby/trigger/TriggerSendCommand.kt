package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandSendEvent

class TriggerSendCommand(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerCommandSend(event: PlayerCommandSendEvent) {
        if (group.players.containsKey(event.player.name)) {
            val roots = args["Root"] as List<String>?
            if (roots.isNullOrEmpty() || roots.contains(event.commands.first { true })) {
                trigger(
                    mutableMapOf(
                        "player" to event.player.name,
                        "root" to event.commands.first { true },
                        "cmds" to event.commands.map { it }
                    )
                )
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "SendCommand"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerSendCommand =
            { group, args, task -> TriggerSendCommand(group, args, task) }
    }
}