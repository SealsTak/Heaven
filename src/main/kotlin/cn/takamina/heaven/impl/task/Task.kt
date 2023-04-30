package cn.takamina.heaven.impl.task

import cn.takamina.heaven.impl.lobby.Lobby

interface Task {
    val lobby: Lobby
    fun call(args: MutableMap<String, Any?>): Any?
}

const val NAMESPACE: String = "heaven"
const val GROUP: String = "group"