package cn.takamina.heaven.impl.kether

import taboolib.library.kether.QuestContext.Frame
import taboolib.module.kether.Script
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.ScriptService
import java.util.*

open class ChainContext(
    script: Script,
    smap: MutableMap<String, Any?>? = null
) : ScriptContext(ScriptService, script) {
    private lateinit var map: SharedMap

    fun getMap(): MutableMap<String, Any?> {
        return map.map
    }

    fun setMap(smap: MutableMap<String, Any?>): MutableMap<String, Any?> {
        val m = map.map
        map.map = smap
        return m
    }

    init {
        smap?.let { setMap(smap) }
    }

    override fun createRootFrame(): Frame? {
        val sharedMap = SharedMap(HashMap())
        return SimpleNamedFrame(
            null,
            LinkedList(),
            SimpleVarTable(null, sharedMap),
            "main",
            this
        ).also {
            map = sharedMap
        }
    }

    class SharedMap(var map: MutableMap<String, Any?>) : MutableMap<String, Any?> {
        override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>>
            get() = map.entries
        override val keys: MutableSet<String>
            get() = map.keys
        override val size: Int
            get() = map.size
        override val values: MutableCollection<Any?>
            get() = map.values


        override fun clear() {
            map.clear()
        }

        override fun isEmpty(): Boolean {
            return map.isEmpty()
        }

        override fun remove(key: String): Any? {
            return map.remove(key)
        }

        override fun putAll(from: Map<out String, Any?>) {
            return map.putAll(from)
        }

        override fun put(key: String, value: Any?): Any? {
            return map.put(key, value)
        }

        override fun get(key: String): Any? {
            return map.get(key)
        }

        override fun containsValue(value: Any?): Boolean {
            return map.containsValue(value)
        }

        override fun containsKey(key: String): Boolean {
            return map.containsKey(key)
        }

    }
}