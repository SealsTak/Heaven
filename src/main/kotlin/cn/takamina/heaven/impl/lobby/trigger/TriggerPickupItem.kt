package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent

class TriggerPickupItem(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val player = event.entity
        if (player is Player && group.players.containsKey(player.name)) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.item.itemStack.type.name)) {
                val names = args["Name"] as List<String>?
                if (names.isNullOrEmpty() || names.contains(
                        (event.item.itemStack.itemMeta?.displayName ?: event.item.name)
                    )
                ) {
                    trigger(
                        mutableMapOf(
                            "player" to player.name,
                            "item" to event.item.itemStack,
                            "amount" to event.item.itemStack.amount,
                            "name" to (event.item.itemStack.itemMeta?.displayName ?: event.item.name),
                            "material" to (event.item.itemStack.type.name),
                        )
                    )
                }
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "PickupItem"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerPickupItem =
            { group, args, task -> TriggerPickupItem(group, args, task) }
    }
}