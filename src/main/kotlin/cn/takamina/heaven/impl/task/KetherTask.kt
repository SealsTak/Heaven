package cn.takamina.heaven.impl.task

import cn.takamina.heaven.impl.lobby.Lobby
import org.bukkit.Bukkit
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherTask(
    override val lobby: Lobby,
    scriptBytes: ByteArray,
) : Task {
    private val script: Script by lazy {
        KetherScriptLoader().load(ScriptService, "Trigger", scriptBytes, arrayListOf(NAMESPACE))
    }

    override fun call(args: MutableMap<String, Any?>): CompletableFuture<Any?>? {
        runCatching {
            return ScriptContext(ScriptService, script).also {
                args.forEach { (k, v) -> it[k] = v }
                it.sender = Bukkit.getPlayer((args["player"] ?: "") as String)?.let { p -> adaptCommandSender(p) }
            }.runActions()
        }.exceptionOrNull()?.printKetherErrorMessage()
        return null
    }
}