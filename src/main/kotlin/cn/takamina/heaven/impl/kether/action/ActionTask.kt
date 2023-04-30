package cn.takamina.heaven.impl.kether.action

import cn.takamina.heaven.impl.lobby.Group
import cn.takamina.heaven.impl.task.GROUP
import cn.takamina.heaven.impl.task.NAMESPACE
import taboolib.common.util.orNull
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionTask(private val func: String, private val task: String, private val args: List<ParsedAction<*>>) :
    ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        fun run(group: Group?, func: String, task: String, args: List<Any>) {
            group?.lobby?.scriptTasks?.get(task)?.callFunction(func, *args.toTypedArray())
            future.complete(null)
        }

        val arguments = LinkedList<Any>()
        fun process(cur: Int) {
            if (cur < args.size) {
                frame.newFrame(args[cur]).run<Any>().thenApply { arg ->
                    arguments.add(arg)
                    if (frame.script().breakLoop) {
                        frame.script().breakLoop = false
                        run(frame.variables().get<Group>(GROUP).orNull(), func, task, arguments)
                    } else {
                        process(cur + 1)
                    }
                }
            } else {
                run(frame.variables().get<Group>(GROUP).orNull(), func, task, arguments)
            }
        }
        process(0)
        /*frame.newFrame(func).run<String>().thenAccept { func ->
            frame.newFrame(task).run<String>().thenApply { task ->

            }
        }*/
        return future
    }

    companion object {
        @KetherParser(["task"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val func = it.nextToken()
            it.expect("in")
            val task = it.nextToken()
            it.expect("with")
            val args = it.next(ArgTypes.listOf(ArgTypes.ACTION))
            it.mark()
            ActionTask(func, task, args)
        }
    }
}