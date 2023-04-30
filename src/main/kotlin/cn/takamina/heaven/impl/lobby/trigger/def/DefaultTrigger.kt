package cn.takamina.heaven.impl.lobby.trigger.def

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.lobby.trigger.AbstractTrigger
import cn.takamina.heaven.impl.lobby.trigger.Trigger
import cn.takamina.heaven.impl.task.Task

abstract class DefaultTrigger(
    group: Group,
    task: Task?
) : AbstractTrigger(group, HashMap(), task) {


    interface DefaultTriggerCreator : TriggerCreator {
        val defaultCreate: (group: Group, task: Task) -> Trigger
    }
}