package cn.takamina.heaven.impl.kether.action

import cn.takamina.heaven.impl.task.NAMESPACE
import org.bukkit.Bukkit
import taboolib.common.platform.function.adaptCommandSender
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionForPlayer(private val values: ParsedAction<*>, val action: ParsedAction<*>) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        val oriSender = frame.script().sender
        frame.newFrame(values).run<List<String?>>().thenApply {
            fun process(cur: Int) {
                if (cur < it.size) {
                    Bukkit.getPlayer(it[cur] ?: "")?.let { player ->
                        frame.script().sender = adaptCommandSender(player)
                        frame.newFrame(action).run<Any>().thenApply {
                            if (frame.script().breakLoop) {
                                frame.script().breakLoop = false
                                frame.script().sender = oriSender
                                future.complete(null)
                            } else {
                                process(cur + 1)
                            }
                        }
                    }
                } else {
                    frame.script().sender = oriSender
                    future.complete(null)
                }
            }
            process(0)
        }
        return future
    }

    companion object {
        @KetherParser(["forp", "forplayer"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionForPlayer(it.run { next(ArgTypes.ACTION) }, it.run {
                expect("then")
                next(ArgTypes.ACTION)
            })
        }
    }
}