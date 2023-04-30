package cn.takamina.heaven.impl.lobby

import cn.takamina.heaven.impl.event.PlayerJoinGroupEvent
import cn.takamina.heaven.impl.event.PlayerLeaveGroupEvent
import cn.takamina.heaven.impl.lobby.trigger.Trigger
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.module.chat.colored
import taboolib.platform.util.attacker
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendInfo
import taboolib.platform.util.sendLang

class Group(val id: String, val lobby: Lobby) : Listener {
    lateinit var name: String

    val players = HashMap<String, Player>()
    val triggers = HashMap<String, Trigger>()
    val rules = HashMap<String, Any?>()
    val set = HashMap<String, Any?>()
    val values = HashMap<String, Any?>()

    internal fun join(player: Player, from: String): Boolean {
        return if (players.containsKey(player.name)) {
            false
        } else {
            players[player.name] = player
            PlayerJoinGroupEvent(player, from, this).call()
            true
        }
    }

    internal fun leave(player: Player, to: String): Boolean {
        return if (players.containsKey(player.name)) {
            PlayerLeaveGroupEvent(player, to, this).call()
            players.remove(player.name)
            true
        } else false
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (players.containsKey(event.player.name)) {
            leave(event.player, Lobby.OUTSIDE)
        }
    }

    @EventHandler
    fun onEntityDamageEntity(event: EntityDamageByEntityEvent) {
        val attacker = event.attacker
        val victim = event.entity
        if (attacker is Player && victim is Player) {
            val a = players.containsKey(attacker.name) // a 在 group 内
            val b = players.containsKey(victim.name) // b 在 group 内
            val c = !a && lobby.containPlayer(attacker) // a 不在 group 内但在 lobby 内
            val d = !b && lobby.containPlayer(victim) // b 不在 group 内但在lobby内
            if (a || b) { // a或b 在 group 内
                if (a == b) { // a和b 都在 group 内
                    event.isCancelled = !((rules["PVP.InGroup"] ?: false) as Boolean)
                } else if (c || d) { // a 不在 group 内但在 lobby 内 或者 b 不在 group 内但在lobby内
                    event.isCancelled = !((rules["PVP.NotInGroup"] ?: false) as Boolean)
                } else { // a 不在 lobby 内但 b 在 group 内 或者 b 不在 lobby 内但 a 在 group 内
                    event.isCancelled = !((rules["PVP.NotInLobby"] ?: false) as Boolean)
                }
            }
        }
    }

    @EventHandler
    fun onCommandSend(event: PlayerCommandPreprocessEvent) {
        if (players.containsKey(event.player.name)) {
            val cmd = event.message.substring(1)
            val disallows = rules["Command.Disallow"] as List<String>?
            if (!event.player.hasPermission("heaven.admin") && !(disallows.isNullOrEmpty() || disallows.any {
                    cmd.startsWith(
                        it
                    ) || it.toRegex().matches(cmd)
                })) {
                event.isCancelled = true
                event.player.sendInfo("Rule-Command-Disallow")
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (rules["Chat.InGroup"] == true && players.containsKey(event.player.name)) {
            event.isCancelled = true
            val prefix = PlaceholderAPI.setPlaceholders(
                event.player,
                (rules["Chat.Format"] ?: "[${name}]%player_name% >> ") as String
            )
            val msg = "$prefix${event.message}".colored()
            val range = (rules["Chat.Range"] ?: -1.0).toString().toDouble()
            players.values.filter { (range < 0) || (it.location.distance(event.player.location) <= range) }
                .forEach { it.sendMessage(msg) }
            if (rules["Chat.Eavesdrop"] == true) {
                onlinePlayers.filter {
                    !players.containsKey(it.name) && it.hasPermission("heaven.eavesdrop.${lobby.id}.$id")
                }.forEach {
                    it.sendLang("Eavesdrop", msg)
                }
            }
        }
    }
}