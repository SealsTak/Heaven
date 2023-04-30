package cn.takamina.heaven

import cn.takamina.heaven.impl.data.Lobbys
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.common.platform.command.*
import taboolib.common.platform.function.submitAsync
import taboolib.expansion.createHelper
import taboolib.module.lang.Language
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendInfo
import java.util.*

@SkipTo(LifeCycle.LOAD)
@CommandHeader(name = "Heaven", permission = "heaven")
object HeavenCommand {

    @CommandBody()
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "heaven.list")
    val list = subCommand {
        execute<CommandSender> { sender, _, _ ->
            val str = LinkedList<String>()
            Lobbys.lobbys.forEach { (_, lobby) ->
                var size = 0
                lobby.groups.forEach { (_, group) ->
                    size += group.players.size
                }
                str.add(sender.asLangText("Command-List-Index", lobby.id, size))
            }
            sender.sendInfo("Command-List-Head")
            str.forEach { sender.sendMessage(it) }
        }
        dynamic(comment = "Lobby", optional = true) {
            suggestion<CommandSender>(false) { _, context ->
                Lobbys.lobbys.keys.filter {
                    context["Lobby"].isEmpty() || it.lowercase().startsWith(context["Lobby"].lowercase())
                }
            }
            execute<CommandSender> { sender, _, argument ->
                val lobby = Lobbys.lobbys[argument]!!
                val str = LinkedList<String>()
                lobby.groups.forEach { (_, group) ->
                    str.add(
                        sender.asLangText(
                            "Command-List-Detail-Group",
                            group.id,
                            group.name,
                            group.players.size,
                            group.players.keys.toTypedArray().contentToString()
                        )
                    )
                }
                sender.sendInfo("Command-List-Detail-Head", lobby.id)
                str.forEach { sender.sendMessage(it) }
            }
        }
    }

    @CommandBody(permission = "heaven.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendInfo("Command-Reload")
            Language.reload()
            Bukkit.getScheduler().callSyncMethod(BukkitPlugin.getInstance()) {
                Lobbys.unload()
            }
            Lobbys.loadAll()
        }
        dynamic(comment = "Lobby", optional = true) {
            suggestion<CommandSender>(false) { _, context ->
                Lobbys.lobbys.keys.filter {
                    context["Lobby"].isEmpty() || it.lowercase().startsWith(context["Lobby"].lowercase())
                }
            }
            execute<CommandSender> { sender, _, argument ->
                sender.sendInfo("Command-Reload-Lobby", argument)
                submitAsync { Lobbys.reload(argument) }
            }
        }
    }

    @CommandBody(permission = "heaven.join")
    val join = subCommand {
        dynamic(comment = "Lobby", optional = false) {
            suggestion<CommandSender>(false) { _, context ->
                val context = context["Lobby"].split(' ')[0]
                Lobbys.lobbys.keys.filter {
                    context.isEmpty() || it.lowercase().startsWith(context.lowercase())
                }
            }
            execute<Player> { sender, context, argument ->
                val lobby = Lobbys.lobbys[argument]!!
                sender.sendInfo(if (lobby.join(sender)) "Command-Join-Success" else "Command-Join-Failure", lobby.id)
            }
            dynamic(comment = "Player", permission = "heaven.join.other", optional = true) {
                suggestion<CommandSender>(false) { _, context ->
                    val context = context["Player"].split(' ')[0]
                    Bukkit.getOnlinePlayers().map { it.name }.filter {
                        context.isEmpty() || it.lowercase().startsWith(context.lowercase())
                    }
                }
                execute<CommandSender> { sender, context, argument ->
                    val lobby = Lobbys.lobbys[context["Lobby"]]!!
                    Bukkit.getPlayer(argument)!!.let { player ->
                        sender.sendInfo(
                            if (lobby.join(player)) "Command-Join-Success-Other" else "Command-Join-Failure-Other",
                            lobby.id,
                            player.name
                        )
                    }
                }
            }
        }
    }

    @CommandBody(permission = "heaven.leave")
    val leave = subCommand {
        dynamic(comment = "Lobby", optional = false) {
            suggestion<CommandSender>(false) { _, context ->
                val context = context["Lobby"].split(' ')[0]
                Lobbys.lobbys.keys.filter { context.isEmpty() || it.lowercase().startsWith(context.lowercase()) }
            }
            execute<Player> { sender, _, argument ->
                val lobby = Lobbys.lobbys[argument]!!
                sender.sendInfo(if (lobby.leave(sender)) "Command-Leave-Success" else "Command-Leave-Failure", lobby.id)
            }
            dynamic(comment = "Player", permission = "heaven.leave.other", optional = true) {
                suggestion<CommandSender>(false) { _, context ->
                    val context = context["Player"].split(' ')[0]
                    Bukkit.getOnlinePlayers().map { it.name }
                        .filter { context.isEmpty() || it.lowercase().startsWith(context.lowercase()) }
                }
                execute<CommandSender> { sender, context, argument ->
                    val lobby = Lobbys.lobbys[context["Lobby"]]!!
                    val player = Bukkit.getPlayer(argument)!!
                    sender.sendInfo(
                        if (lobby.leave(player)) "Command-Leave-Success-Other" else "Command-Leave-Failure-Other",
                        lobby.id,
                        player.name
                    )
                }
            }
        }
    }
}