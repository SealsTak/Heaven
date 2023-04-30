package cn.takamina.heaven.impl.event

import cn.takamina.heaven.impl.lobby.Group
import taboolib.platform.type.BukkitProxyEvent

class GroupLoadEvent(
    val group: Group
) : BukkitProxyEvent()