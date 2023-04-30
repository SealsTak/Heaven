package cn.takamina.heaven.impl.data

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.lobby.Lobby
import cn.takamina.heaven.impl.lobby.trigger.Triggers
import cn.takamina.heaven.impl.task.*
import com.typesafe.config.ConfigFactory
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Awake
import taboolib.common.platform.SkipTo
import taboolib.common.platform.function.*
import taboolib.module.lang.sendError
import taboolib.module.lang.sendInfo
import taboolib.module.lang.sendWarn
import taboolib.platform.BukkitPlugin
import java.io.File
import java.nio.charset.StandardCharsets

@SkipTo(LifeCycle.LOAD)
@RuntimeDependency("com.typesafe:config:1.4.2")
object Lobbys {
    val lobbys = HashMap<String, Lobby>()

    @Awake(LifeCycle.ACTIVE)
    fun loadAll() = submitAsync {
        console().sendInfo("Lobby-Loading")
        val lobbysFolder = File(getDataFolder(), "lobby")
        lobbys.values.forEach { it.disable() }
        lobbys.clear()
        if (!lobbysFolder.exists()) {
            lobbysFolder.mkdirs()
            releaseResourceFile("lobby/Example/Main.conf", true)
            releaseResourceFile("lobby/Example/Group1.conf", true)
            releaseResourceFile("lobby/Example/task/JavaScriptTask.js", true)
            releaseResourceFile("lobby/Example/task/LuaTask.lua", true)
            releaseResourceFile("lobby/Example/task/PythonTask.py", true)
        }

        lobbysFolder.listFiles()?.filter { it.isDirectory }?.forEach { folder ->
            load(folder)?.let { lobby ->
                lobbys[lobby.id] = lobby
            }
        }

        Bukkit.getScheduler().callSyncMethod(BukkitPlugin.getInstance()) {
            lobbys.values.forEach { it.enable() }
        }
    }

    fun unload() {
        lobbys.values.forEach { it.disable() }
        lobbys.clear()
    }

    fun load(folder: File): Lobby? {
        val lobby = Lobby(folder.name)
        console().sendInfo("Lobby-Load-Lobby", lobby.id)
        folder.listFiles()?.filter { it.isFile && it.name.endsWith(".conf") }?.forEach { groupFile ->
            val groupConfig = ConfigFactory.parseFile(groupFile)

            val group = Group(groupFile.nameWithoutExtension, lobby)
            lobby.groups[group.id] = group
            group.name = groupConfig.getString("Name")
            console().sendInfo("Lobby-Load-Group", lobby.id, group.id)
            groupConfig.getConfig("Trigger").root().forEach { id, _ ->
                val node = groupConfig.getConfig("Trigger.$id")
                Triggers.getTriggerCreator(node.getString("Type"))?.let { create ->
                    val args = if (node.hasPath("Args")) {
                        node.getValue("Args").unwrapped() as MutableMap<String, Any>
                    } else {
                        HashMap()
                    }
                    node.root().forEach {
                        when (it.key.uppercase()) {
                            "JS", "JAVASCRIPT" -> JSTask(lobby, it.value.unwrapped() as String)
                            "PY", "PYTHON" -> PythonTask(lobby, it.value.unwrapped() as String)
                            "LUA" -> LuaTask(lobby, it.value.unwrapped() as String)
                            "KS", "KETHER" -> KetherTask(
                                lobby,
                                "def main = {\n${(it.value.unwrapped() as String)}\n}".toByteArray(StandardCharsets.UTF_8)
                            )

                            else -> null
                        }?.let { task ->
                            group.triggers.putIfAbsent("${id}_${it.key}", create(group, args, task))
                        }
                    }
                }
            }

            if (groupConfig.hasPath("Rule")) {
                groupConfig.getConfig("Rule").entrySet().forEach {
                    group.rules[it.key] = it.value.unwrapped()
                }
            }
            if (groupConfig.hasPath("Set")) {
                groupConfig.getConfig("Set").entrySet().forEach {
                    group.set[it.key] = it.value.unwrapped()
                }
            }
            if (groupConfig.hasPath("Command")) {
                groupConfig.getConfig("Command").entrySet().forEach {
                    group.set[it.key] = it.value.unwrapped()
                }
            }

            groupConfig.root().filter {
                Triggers.getDeafultTriggerCreator(it.key) != null
            }.forEach { (key, _) ->
                groupConfig.getConfig(key).entrySet().forEach {
                    when (it.key.uppercase()) {
                        "JS", "JAVASCRIPT" -> JSTask(lobby, it.value.unwrapped() as String)
                        "PY", "PYTHON" -> PythonTask(lobby, it.value.unwrapped() as String)
                        "LUA" -> LuaTask(lobby, it.value.unwrapped() as String)
                        "KS", "KETHER" -> KetherTask(
                            lobby,
                            "def main = {\n${(it.value.unwrapped() as String)}\n}".toByteArray(StandardCharsets.UTF_8)
                        )

                        else -> null
                    }?.let { task ->
                        Triggers.getDeafultTriggerCreator(key)?.let { create ->
                            create(group, task)
                        }?.let { trigger ->
                            group.triggers.putIfAbsent("${key}_${it.key}", trigger)
                        }
                    }
                }
            }
        }

        val taskFolder = File(folder, "task")
        if (taskFolder.exists() && taskFolder.isDirectory) {
            fun loadTask(file: File) {
                if (file.isFile) {
                    when (file.extension.uppercase()) {
                        "JS" -> JSTask(lobby, file.readText(StandardCharsets.UTF_8))
                        "LUA" -> LuaTask(lobby, file.readText(StandardCharsets.UTF_8))
                        "PY" -> PythonTask(lobby, file.readText(StandardCharsets.UTF_8))
                        else -> null
                    }?.let { task ->
                        console().sendInfo("Lobby-Load-Task", lobby.id, file.name)
                        lobby.scriptTasks.putIfAbsent(file.nameWithoutExtension, task)?.let { _ ->
                            console().sendWarn("Task-Name-Exist", file.nameWithoutExtension)
                        }
                    }
                } else {
                    file.listFiles()?.forEach { loadTask(it) }
                }
            }
            loadTask(taskFolder)
        }
        return if (!lobby.groups.containsKey("Main")) {
            console().sendError("Lobby-Missing-Main", lobby.id)
            null
        } else {
            lobby
        }
    }

    fun reload(lobby: String) {
        lobbys[lobby]?.let {
            it.disable()
            lobbys.remove(lobby)
            load(File(getDataFolder(), "lobby/${it.id}"))?.let {
                lobbys[lobby] = it
                it.enable()
            }
        }
    }
}