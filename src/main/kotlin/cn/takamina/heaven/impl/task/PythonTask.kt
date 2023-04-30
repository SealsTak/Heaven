package cn.takamina.heaven.impl.task

import cn.takamina.heaven.impl.lobby.Lobby
import org.bukkit.Bukkit
import org.python.core.Py
import org.python.core.PyFunction
import org.python.core.PyStringMap
import org.python.util.PythonInterpreter
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.sendError
import taboolib.module.lang.sendWarn
import taboolib.module.lang.sendWarnMessage

@RuntimeDependency("org.python:jython-standalone:2.7.3")
class PythonTask(
    override val lobby: Lobby,
    script: String,
) : ScriptTask() {
    companion object {
        lateinit var interpreter: PythonInterpreter

        init {
            submitAsync(now = true) {
                interpreter = PythonInterpreter()
            }
        }
    }

    private val script by lazy {
        load()
        this["thisPy"] = this
        interpreter.compile(script)
    }

    private val context by lazy {
        PyStringMap()
    }

    override fun set(key: String, value: Any?) {
        context.map[key] = Py.java2py(value)
    }

    override fun importClass(key: String, classPath: String) {
        runCatching {
            set(key, Class.forName(classPath))
        }.exceptionOrNull()?.let { err ->
            console().sendError("Py-Import-Error", lobby.id, classPath)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
    }

    override fun callFunction(name: String, vararg params: Any?): Any? {
        context.map[name]?.let {
            if (it is PyFunction) {
                return it.__call__(params.map { Py.java2py(it) }.toTypedArray())
            } else {
                console().sendError("Py-CallFunction-Error", lobby.id, name)
                console().sendWarn("Py-Function-NotExist", name)
            }
        }
        return null
    }

    override fun call(args: MutableMap<String, Any?>): Any? {
        this["player"] = Bukkit.getPlayer((args["player"] ?: "") as String)
        return Py.runCode(script, context, context)
    }

}