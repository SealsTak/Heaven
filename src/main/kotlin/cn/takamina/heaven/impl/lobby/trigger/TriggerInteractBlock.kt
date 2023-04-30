package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

class TriggerInteractBlock(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (group.players.containsKey(player.name) && event.hasBlock()) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.clickedBlock!!.type.name)) {
                trigger(
                    mutableMapOf(
                        "player" to player.name,
                        "type" to event.clickedBlock!!.type.name,
                        "face" to event.blockFace.name,
                        "x" to event.clickedBlock!!.x,
                        "y" to event.clickedBlock!!.y,
                        "z" to event.clickedBlock!!.z,
                        "click" to event.action.name,
                    )
                )
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "InteractBlock"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerInteractBlock =
            { group, args, task -> TriggerInteractBlock(group, args, task) }
    }
}