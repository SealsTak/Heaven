package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler
import org.bukkit.event.player.AsyncPlayerChatEvent

class TriggerChat(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (group.players.containsKey(player.name)) {
            val prefixs = args["Prefix"] as List<String>?
            if (prefixs.isNullOrEmpty() || prefixs.any { event.message.startsWith(it) }) {
                trigger(
                    hashMapOf(
                        "player" to event.player.name,
                        "msg" to event.message,
                    )
                )
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "Chat"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerChat =
            { group, args, task -> TriggerChat(group, args, task) }
    }
}