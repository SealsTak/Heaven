package cn.takamina.heaven.impl.task

import cn.takamina.heaven.impl.lobby.Lobby
import cn.takamina.heaven.utils.NashornUtil
import org.bukkit.Bukkit
import taboolib.common.platform.function.console
import taboolib.module.lang.sendError
import taboolib.module.lang.sendWarnMessage
import javax.script.Compilable
import javax.script.Invocable

class JSTask(
    override val lobby: Lobby,
    script: String,
) : ScriptTask() {
    val engine by lazy {
        NashornUtil.getEngine()
    }

    private val script by lazy {
        load()
        this["thisJS"] = this
        (engine as Compilable).compile(script)
    }

    override fun set(key: String, obj: Any?) {
        engine.put(key, obj)
    }

    override fun importClass(key: String, classPath: String) {
        runCatching {
            engine.eval("var $key = Java.type(\"$classPath\");")
        }.exceptionOrNull()?.let { err ->
            console().sendError("JS-Import-Error", lobby.id, classPath)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
    }

    override fun callFunction(name: String, vararg params: Any?): Any? {
        runCatching {
            return (engine as Invocable).invokeFunction(name, params)
        }.exceptionOrNull()?.let { err ->
            console().sendError("JS-CallFunction-Error", lobby.id, name)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
        return false
    }

    override fun call(args: MutableMap<String, Any?>): Any? {
        runCatching {
            args.forEach {
                this[it.key] = it.value
            }
            this["player"] = Bukkit.getPlayer((args["player"] ?: "") as String)
            return script.eval()
        }.exceptionOrNull()?.let { err ->
            console().sendError("JS-Call-Error", lobby.id)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
        return null
    }
}