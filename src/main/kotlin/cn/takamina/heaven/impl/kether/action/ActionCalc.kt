package cn.takamina.heaven.impl.kether.action

import cn.takamina.heaven.impl.task.NAMESPACE
import cn.takamina.heaven.utils.math.operation.MathFunction

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.KetherParser
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionCalc(private val expression: ParsedAction<*>) : ScriptAction<Double>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Double> {
        return frame.newFrame(expression).run<String>().thenApply { expression ->
            MathFunction().analyze(expression)()
        }
    }

    companion object {
        @KetherParser(["calc"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val expression = it.next(ArgTypes.ACTION)
            ActionCalc(expression)
        }
    }
}