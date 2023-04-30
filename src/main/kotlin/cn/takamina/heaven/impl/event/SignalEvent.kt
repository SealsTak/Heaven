package cn.takamina.heaven.impl.event

import cn.takamina.heaven.impl.lobby.Lobby
import taboolib.platform.type.BukkitProxyEvent

class SignalEvent(
    val signal: String,
    val lobby: Lobby
) : BukkitProxyEvent()