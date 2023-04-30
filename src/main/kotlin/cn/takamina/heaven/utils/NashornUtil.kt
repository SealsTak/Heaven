package cn.takamina.heaven.utils

import taboolib.common.env.RuntimeDependency
import javax.script.ScriptEngine


object NashornUtil {
    private val hasNashorn by lazy {
        runCatching { Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory") }.exceptionOrNull() == null
    }

    fun getEngine(): ScriptEngine {
        if (!hasNashorn) {
            return OuterNashorn.getEngine()
        }
        return InnerNashorn.getEngine()
    }
}

@RuntimeDependency("org.openjdk.nashorn:nashorn-core:15.4")
object OuterNashorn {
    fun getEngine(): ScriptEngine {
        val clazz = Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory")
        val factory = clazz.getDeclaredConstructor().newInstance()
        val engine = clazz.getDeclaredMethod("getScriptEngine").invoke(factory)
        return (engine as ScriptEngine?)!!
    }

    fun getEngine(classLoader: ClassLoader): ScriptEngine {
        val clazz = Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory")
        val factory = clazz.getDeclaredConstructor().newInstance()
        val engine = clazz.getDeclaredMethod("getScriptEngine", ClassLoader::class.java).invoke(factory, classLoader)
        return (engine as ScriptEngine?)!!
    }
}

object InnerNashorn {
    fun getEngine(): ScriptEngine {
        val clazz = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory")
        val factory = clazz.getDeclaredConstructor().newInstance()
        val engine = clazz.getDeclaredMethod("getScriptEngine").invoke(factory)
        return (engine as ScriptEngine?)!!
    }

    fun getEngine(classLoader: ClassLoader): ScriptEngine {
        val clazz = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory")
        val factory = clazz.getDeclaredConstructor().newInstance()
        val engine = clazz.getDeclaredMethod("getScriptEngine", ClassLoader::class.java).invoke(factory, classLoader)
        return (engine as ScriptEngine?)!!
    }
}