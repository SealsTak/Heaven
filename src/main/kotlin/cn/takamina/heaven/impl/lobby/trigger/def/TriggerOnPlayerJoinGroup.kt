package cn.takamina.heaven.impl.lobby.trigger.def

import cn.takamina.heaven.impl.event.PlayerJoinGroupEvent
import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler

class TriggerOnPlayerJoinGroup(
    group: Group,
    task: Task
) : DefaultTrigger(group, task) {

    @EventHandler
    fun onPlayerJoinGroup(event: PlayerJoinGroupEvent) {
        if (group.id == event.group.id && group.lobby.id == event.group.lobby.id) {
            trigger(
                mutableMapOf(
                    "player" to event.player.name,
                    "from" to event.from
                )
            )
        }
    }


    companion object : DefaultTriggerCreator {
        override val id = "OnPlayerJoin"
        override val defaultCreate: (group: Group, task: Task) -> TriggerOnPlayerJoinGroup =
            { group, task -> TriggerOnPlayerJoinGroup(group, task) }
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerOnPlayerJoinGroup =
            { group, _, task -> TriggerOnPlayerJoinGroup(group, task) }
    }
}