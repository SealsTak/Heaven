package cn.takamina.heaven.impl.lobby.trigger.def

import cn.takamina.heaven.impl.event.GroupLoadEvent
import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler

class TriggerOnGroupLoad(
    group: Group,
    task: Task
) : DefaultTrigger(group, task) {

    @EventHandler
    fun onGroupLoad(event: GroupLoadEvent) {
        if (group.id == event.group.id && group.lobby.id == event.group.lobby.id) {
            trigger(mutableMapOf())
        }
    }

    companion object : DefaultTriggerCreator {
        override val id = "OnGroupLoad"
        override val defaultCreate: (group: Group, task: Task) -> TriggerOnGroupLoad =
            { group, task -> TriggerOnGroupLoad(group, task) }
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerOnGroupLoad =
            { group, _, task -> TriggerOnGroupLoad(group, task) }
    }
}