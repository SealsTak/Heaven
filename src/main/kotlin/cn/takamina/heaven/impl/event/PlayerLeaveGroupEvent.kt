package cn.takamina.heaven.impl.event

import cn.takamina.heaven.impl.lobby.Group
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerLeaveGroupEvent(
    val player: Player,
    val to: String,
    val group: Group
) : BukkitProxyEvent()