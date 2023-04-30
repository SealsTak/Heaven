package cn.takamina.heaven.impl.kether.action

import cn.takamina.heaven.impl.task.NAMESPACE
import org.bukkit.Bukkit
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.time.Duration
import java.util.concurrent.CompletableFuture

class ActionSchedule(private val time: Duration, private val action: ParsedAction<*>) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(time.toString()).run<Unit>().thenAccept {
            val tick = time.toMillis() / 50L
            val oriSender = frame.script().sender?.name ?: ""
            submit(delay = tick, async = !isPrimaryThread) {
                val curSender = frame.script().sender?.name ?: ""
                Bukkit.getPlayer(oriSender)?.let {
                    frame.script().sender = adaptCommandSender(it)
                    frame.newFrame(action).run<Any>().thenApply {
                        Bukkit.getPlayer(curSender)?.let { frame.script().sender = adaptCommandSender(it) }
                    }
                }
            }
        }
    }

    companion object {
        @KetherParser(["schedule"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val time = it.next(ArgTypes.DURATION)
            it.expect("then")
            ActionSchedule(time, it.nextParsedAction() as ParsedAction<*>)
        }
    }
}