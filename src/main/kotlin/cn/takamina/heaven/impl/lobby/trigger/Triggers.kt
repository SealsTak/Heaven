package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.lobby.trigger.def.DefaultTrigger
import cn.takamina.heaven.impl.lobby.trigger.def.TriggerOnGroupLoad
import cn.takamina.heaven.impl.lobby.trigger.def.TriggerOnPlayerJoinGroup
import cn.takamina.heaven.impl.lobby.trigger.def.TriggerOnPlayerLeaveGroup
import cn.takamina.heaven.impl.task.Task

object Triggers {
    private val defaultTriggers = HashMap<String, (group: Group, taks: Task) -> Trigger>()
    private val triggers = HashMap<String, (group: Group, args: Map<String, Any>, taks: Task) -> Trigger>()

    init {
        registerDefaultTrigger(TriggerOnGroupLoad)
        registerDefaultTrigger(TriggerOnPlayerJoinGroup)
        registerDefaultTrigger(TriggerOnPlayerLeaveGroup)

        registerTrigger(TriggerOnGroupLoad)
        registerTrigger(TriggerOnPlayerJoinGroup)
        registerTrigger(TriggerOnPlayerLeaveGroup)

        registerTrigger(TriggerDamage)
        registerTrigger(TriggerBeDamaged)
        registerTrigger(TriggerBreakBlock)
        registerTrigger(TriggerChat)
        registerTrigger(TriggerSignal)
        registerTrigger(TriggerDeath)
        registerTrigger(TriggerInteractEntity)
        registerTrigger(TriggerInteractBlock)
        registerTrigger(TriggerKillPlayer)
        registerTrigger(TriggerKillEntity)
        registerTrigger(TriggerPickupItem)
        registerTrigger(TriggerSendCommand)
        registerTrigger(TriggerToggleSneak)
        registerTrigger(TriggerEnterArea)
        registerTrigger(TriggerLeaveArea)
    }

    fun getTriggerCreator(type: String): ((group: Group, args: Map<String, Any>, task: Task) -> Trigger)? {
        return triggers[type.uppercase()]
    }

    fun getDeafultTriggerCreator(type: String): ((group: Group, task: Task) -> Trigger)? {
        return defaultTriggers[type.uppercase()]
    }

    fun registerTrigger(creator: Trigger.TriggerCreator) {
        triggers[creator.id.uppercase()] = creator.create
    }

    fun registerDefaultTrigger(creator: DefaultTrigger.DefaultTriggerCreator) {
        defaultTriggers[creator.id.uppercase()] = creator.defaultCreate
    }
}