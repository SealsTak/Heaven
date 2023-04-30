package cn.takamina.heaven.impl.task

import cn.takamina.heaven.impl.lobby.Lobby
import org.bukkit.Bukkit
import org.luaj.vm2.LuaClosure
import org.luaj.vm2.LuaValue
import org.luaj.vm2.script.LuaScriptEngine
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.function.console
import taboolib.module.lang.sendError
import taboolib.module.lang.sendWarnMessage
import javax.script.Compilable

@RuntimeDependency("org.luaj:luaj-jse:3.0.1")
class LuaTask(
    override val lobby: Lobby,
    script: String,
) : ScriptTask() {
    private val engine by lazy {
        LuaScriptEngine()
    }

    private val script by lazy {
        load()
        this["thisLua"] = this
        (engine as Compilable).compile(script)
    }

    override fun set(key: String, value: Any?) {
        engine.put(key, value)
    }

    override fun importClass(key: String, classPath: String) {
        runCatching {
            set(key, Class.forName(classPath))
        }.exceptionOrNull()?.let { err ->
            console().sendError("Lua-Import-Error", lobby.id, classPath)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
    }

    override fun callFunction(name: String, vararg params: Any?): Any? {
        runCatching {
            return (engine.get(name) as LuaClosure?)?.invoke(params.map { LuaValue.userdataOf(it) }.toTypedArray())
        }.exceptionOrNull()?.let { err ->
            console().sendError("Lua-CallFunction-Error", lobby.id, name)
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
            console().sendError("Lua-Call-Error", lobby.id)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
        return null
    }


}