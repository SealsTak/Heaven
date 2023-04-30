package cn.takamina.heaven.impl.task

import cn.takamina.heaven.impl.event.SignalEvent
import cn.takamina.heaven.utils.ListenerFactory
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.lang.sendError
import taboolib.module.lang.sendWarnMessage
import taboolib.platform.BukkitPlugin
import java.util.*
import java.util.function.Consumer

abstract class ScriptTask : Task {
    val listeners = LinkedList<Listener>()
    abstract operator fun set(key: String, value: Any?)
    abstract fun importClass(key: String, classPath: String)
    abstract fun callFunction(name: String, vararg params: Any?): Any?

    fun load() {
        importClass("Bukkit", Bukkit::class.qualifiedName!!)
        importClass("Location", Location::class.qualifiedName!!)
        importClass("Random", Random::class.qualifiedName!!)
        importClass("Arrays", Arrays::class.qualifiedName!!)
        importClass("Class", Class::class.qualifiedName!!)
        importClass("System", System::class.qualifiedName!!)
        importClass("Math", Math::class.qualifiedName!!)
        importClass("Player", Player::class.qualifiedName!!)
        importClass("Entity", Entity::class.qualifiedName!!)

        importClass("String", "java.lang.String")
        importClass("Boolean", "java.lang.Boolean")
        importClass("Double", "java.lang.Double")
        importClass("Short", "java.lang.Short")
        importClass("Byte", "java.lang.Byte")
        importClass("Integer", "java.lang.Integer")
        importClass("Float", "java.lang.Float")
        importClass("Long", "java.lang.Long")
        importClass("Object", "java.lang.Object")
    }

    fun disable() {
        listeners.forEach { HandlerList.unregisterAll(it) }
    }

    fun createListener(classPath: String, method: Consumer<out Event>) {
        try {
            val eventClass = Class.forName(classPath) as Class<out Event>
            val listener: Listener = ListenerFactory.getListener(eventClass, method)!!
            listeners.add(listener)
            Bukkit.getPluginManager().registerEvents(listener, BukkitPlugin.getInstance())
        } catch (err: ClassNotFoundException) {
            console().sendError("Listener-Create-Class-NotFound", classPath)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
    }

    fun createListener(classPath: String, function: String) {
        try {
            val eventClass = Class.forName(classPath) as Class<out Event>
            val listener: Listener =
                ListenerFactory.getListener(eventClass) { event -> callFunction(function, event) }!!
            listeners.add(listener)
            Bukkit.getPluginManager().registerEvents(listener, BukkitPlugin.getInstance())
        } catch (err: ClassNotFoundException) {
            console().sendError("Listener-Create-Class-NotFound", classPath)
            console().sendWarnMessage(err.message ?: "Error", err.stackTrace)
        }
    }

    fun isInstance(classpath: String?, obj: Any?): Boolean {
        runCatching { return Class.forName(classpath).isInstance(obj) }
        return false
    }

    fun parsePlaceholde(text: String, player: Player): String {
        return PlaceholderAPI.setPlaceholders(player, text)
    }

    fun runTaskLater(func: String, async: Boolean, delay: Int, vararg paras: Any?) {
        submit(delay = delay.toLong(), async = async) {
            callFunction(func, *paras)
        }
    }

    fun runTaskTimer(func: String, async: Boolean, delay: Int, period: Int, vararg paras: Any?) {
        submit(delay = delay.toLong(), period = period.toLong(), async = async) {
            callFunction(func, *paras)
        }
    }

    fun signal(signal: String) {
        SignalEvent(signal, lobby).call()
    }
}