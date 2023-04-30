package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class TriggerBreakBlock(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        if (group.players.containsKey(player.name)) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.block.type.name)) {
                trigger(
                    mutableMapOf(
                        "player" to player.name,
                        "type" to event.block.type.name,
                        "x" to event.block.x,
                        "y" to event.block.y,
                        "z" to event.block.z,
                        "exp" to event.expToDrop,
                    )
                )
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "BreakBlock"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerBreakBlock =
            { group, args, task -> TriggerBreakBlock(group, args, task) }
    }
}