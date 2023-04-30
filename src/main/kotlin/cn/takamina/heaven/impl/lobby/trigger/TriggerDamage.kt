package cn.takamina.heaven.impl.lobby.trigger

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.Task
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.platform.util.attacker

class TriggerDamage(
    group: Group,
    args: Map<String, Any>,
    task: Task
) : AbstractTrigger(group, args, task) {

    @EventHandler
    fun onEntityDamageEntity(event: EntityDamageByEntityEvent) {
        val player = event.attacker
        if (player is Player && group.players.containsKey(player.name)) {
            val types = args["Type"] as List<String>?
            if (types.isNullOrEmpty() || types.contains(event.entity.type.name)) {
                val names = args["Name"] as List<String>?
                if (names.isNullOrEmpty() || names.contains(event.entity.name)) {
                    trigger(
                        mutableMapOf(
                            "player" to player.name,
                            "type" to event.entity.type.name,
                            "name" to event.entity.entityId,
                            "cause" to event.cause.name,
                            "damage" to event.damage,
                            "final_damage" to event.finalDamage
                        )
                    )
                }
            }
        }
    }

    companion object : TriggerCreator {
        override val id = "Damage"
        override val create: (group: Group, args: Map<String, Any>, task: Task) -> TriggerDamage =
            { group, args, task -> TriggerDamage(group, args, task) }
    }
}