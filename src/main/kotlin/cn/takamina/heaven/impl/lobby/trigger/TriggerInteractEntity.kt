package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import taboolib.platform.util.attacker

class TriggerInteractEntity(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player
        if (group.players.containsKey(player.name)) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.rightClicked.type.name)) {
                val names = args["Name"] as List<String>?
                if (names.isNullOrEmpty() || names.contains(event.rightClicked.name)) {
                    trigger(
                        mutableMapOf(
                            "player" to player.name,
                            "name" to event.rightClicked.name,
                            "type" to event.rightClicked.type.name,
                            "click" to "Right",
                        )
                    )
                }
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val player = event.attacker
        if (player is Player && group.players.containsKey(player.name)) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.entity.type.name)) {
                val names = args["Name"] as List<String>?
                if (names.isNullOrEmpty() || names.contains(event.entity.name)) {
                    trigger(
                        mutableMapOf(
                            "player" to player.name,
                            "name" to event.entity.name,
                            "type" to event.entity.type.name,
                            "click" to "Left",
                        )
                    )
                }
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "InteractEntity"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerInteractEntity =
            { group, args, task -> TriggerInteractEntity(group, args, task) }
    }
}