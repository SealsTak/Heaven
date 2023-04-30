package cn.takamina.heaven.impl.kether

import taboolib.common.LifeCycle
import taboolib.common.platform.SkipTo
import taboolib.module.kether.*

@SkipTo(LifeCycle.ACTIVE)
object KetherManager {

    fun ScriptContext.Companion.subCreate(
        script: Script,
        context: ScriptContext,
        run: ScriptContext.() -> Unit = {}
    ): ScriptContext {
        return ScriptContext(ScriptService, script).also { sc ->
            context.rootFrame().variables().values().forEach {
                sc.rootFrame().variables().set(it.key, it.value)
            }
        }.also(run)
    }
}

const val SMAP: String = "@Heaven-SMap"
