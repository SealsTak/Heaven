package cn.takamina.heaven.impl.lobby.trigger.def

import cn.takamina.heaven.impl.event.PlayerLeaveGroupEvent
import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler

class TriggerOnPlayerLeaveGroup(
    group: Group,
    task: Task
) : DefaultTrigger(group, task) {

    @EventHandler
    fun onPlayerLeaveGroup(event: PlayerLeaveGroupEvent) {
        if (group.id == event.group.id && group.lobby.id == event.group.lobby.id) {
            trigger(
                mutableMapOf(
                    "player" to event.player.name,
                    "to" to event.to
                )
            )
        }
    }


    companion object : DefaultTriggerCreator {
        override val id = "OnPlayerLeave"
        override val defaultCreate: (group: Group, task: Task) -> TriggerOnPlayerLeaveGroup =
            { group, task -> TriggerOnPlayerLeaveGroup(group, task) }
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerOnPlayerLeaveGroup =
            { group, _, task -> TriggerOnPlayerLeaveGroup(group, task) }
    }
}