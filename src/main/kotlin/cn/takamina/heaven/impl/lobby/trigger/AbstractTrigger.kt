package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.GROUP
import cn.takamina.heaven.impl.task.Task

abstract class AbstractTrigger(
    group: Group,
    args: Map<String, Any>,
    private val task: Task?
) : Trigger(group, args) {
    override fun trigger(args: MutableMap<String, Any?>) {
        args[GROUP] = group
        task?.call(args)
    }
}