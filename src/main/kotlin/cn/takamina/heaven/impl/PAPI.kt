package cn.takamina.heaven.impl

import cn.takamina.heaven.impl.data.Lobbys
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PAPI : PlaceholderExpansion {
    override val identifier: String
        get() = "heaven"

    override val autoReload: Boolean
        get() = true

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        val args = args.split('_')
        return when (args[0].lowercase()) {
            "lvalue" -> Lobbys.lobbys[args.getOrNull(1) ?: ""]?.values?.get(args.getOrNull(2) ?: "")?.toString() ?: ""
            "gvalue" -> Lobbys.lobbys[args.getOrNull(1) ?: ""]?.groups?.get(
                args.getOrNull(2) ?: ""
            )?.values?.get(args.getOrNull(3) ?: "")?.toString() ?: ""

            else -> "null"
        }
    }
}