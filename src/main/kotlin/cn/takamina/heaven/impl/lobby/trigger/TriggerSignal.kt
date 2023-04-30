package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.event.SignalEvent
import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler

class TriggerSignal(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onSignal(event: SignalEvent) {
        if (event.lobby.id == group.lobby.id) {
            val ids = args["ID"] as List<String>?
            if (ids.isNullOrEmpty() || ids.contains(event.signal)) {
                trigger(
                    mutableMapOf(
                        "signal" to event.signal,
                    )
                )
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "Signal"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerSignal =
            { group, args, task -> TriggerSignal(group, args, task) }
    }
}