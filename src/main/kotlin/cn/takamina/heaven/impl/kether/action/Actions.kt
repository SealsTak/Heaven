package cn.takamina.heaven.impl.kether.action

import cn.takamina.heaven.impl.data.Lobbys
import cn.takamina.heaven.impl.event.SignalEvent
import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.GROUP
import cn.takamina.heaven.impl.task.NAMESPACE
import org.bukkit.Bukkit
import taboolib.common.util.orNull
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.script
import java.util.*

object Actions {

    @KetherParser(["group"], namespace = NAMESPACE, shared = true)
    fun group() = combinationParser {
        it.group(text(), text()).apply(it) { symbol, target ->
            now {
                when (symbol.lowercase()) {
                    "list" -> this.variables().get<Group>(GROUP).orNull()?.players?.entries?.map { it.key }
                        ?: arrayListOf("")

                    "join" -> Bukkit.getPlayer(script().sender?.name ?: "")?.let { player ->
                        this.variables().get<Group>(GROUP).orNull()?.let { group ->
                            group.lobby.join(player, target)
                        }
                    } ?: false

                    else -> listOf("")
                }
            }
        }
    }

    @KetherParser(["lobby"], namespace = NAMESPACE, shared = true)
    fun lobby() = combinationParser {
        it.group(text()).apply(it) { symbol ->
            now {
                when (symbol.lowercase()) {
                    "list" -> this.variables().get<Group>(GROUP).orNull()?.lobby?.let { lobby ->
                        val list = LinkedList<String>()
                        lobby.groups.values.forEach { list.addAll(it.players.keys.toList()) }
                        list
                    }

                    "leave" -> Bukkit.getPlayer(script().sender?.name ?: "")?.let { player ->
                        this.variables().get<Group>(GROUP).orNull()?.lobby?.leave(player)
                    } ?: false

                    "playing" -> Bukkit.getPlayer(script().sender?.name ?: "")?.let { player ->
                        Lobbys.lobbys.values.filter { it.groups.values.any { it.players.keys.contains(player.name) } }
                            .map { it.id }
                    } ?: arrayListOf<String>()

                    else -> listOf("")
                }
            }
        }
    }

    @KetherParser(["group_value"], namespace = NAMESPACE, shared = true)
    fun groupValue() = combinationParser {
        it.group(text(), command("set", then = any()).option()).apply(it) { key, value ->
            now {
                value?.let { value ->
                    this.variables().get<Group>(GROUP).orNull()?.values?.set(key, value)
                } ?: let {
                    this.variables().get<Group>(GROUP).orNull()?.values?.get(key)
                }
            }
        }
    }

    @KetherParser(["lobby_value"], namespace = NAMESPACE, shared = true)
    fun lobbyValue() = combinationParser {
        it.group(text(), command("set", then = any()).option()).apply(it) { key, value ->
            now {
                value?.let { value ->
                    this.variables().get<Group>(GROUP).orNull()?.lobby?.values?.set(key, value)
                } ?: let {
                    this.variables().get<Group>(GROUP).orNull()?.lobby?.values?.get(key)
                }
            }
        }
    }

    @KetherParser(["signal"], namespace = NAMESPACE, shared = true)
    fun signal() = combinationParser {
        it.group(text()).apply(it) { signal ->
            now {
                this.variables().get<Group>(GROUP).orNull()?.let {
                    SignalEvent(signal, it.lobby).call()
                }
            }
        }
    }

    @KetherParser(["task"], namespace = NAMESPACE, shared = true)
    fun task() = combinationParser {
        it.group(any(), command("in", then = any()), command("with", then = anyList())).apply(it) { func, task, args ->
            now {
                variables().get<Group>(GROUP).orNull()?.let { group ->
                    group.lobby.scriptTasks[task.toString()]?.let { task ->
                        task.callFunction(func.toString(), args.toTypedArray())
                    }
                }
            }
        }
    }
}