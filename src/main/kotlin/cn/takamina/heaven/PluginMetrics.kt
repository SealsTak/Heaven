package cn.takamina.heaven

import cn.takamina.heaven.impl.data.Lobbys
import cn.takamina.heaven.impl.task.JSTask
import cn.takamina.heaven.impl.task.LuaTask
import cn.takamina.heaven.impl.task.PythonTask
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.SingleLineChart
import taboolib.platform.BukkitPlugin

object PluginMetrics {
    private lateinit var metrics: Metrics

    @Awake(LifeCycle.ACTIVE)
    private fun init() {
        metrics = Metrics(18332, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
        metrics.addCustomChart(SingleLineChart("script_js") {
            Lobbys.lobbys.values.sumOf { it.scriptTasks.values.filter { it is JSTask }.size }
        })
        metrics.addCustomChart(SingleLineChart("script_py") {
            Lobbys.lobbys.values.sumOf { it.scriptTasks.values.filter { it is PythonTask }.size }
        })
        metrics.addCustomChart(SingleLineChart("script_lua") {
            Lobbys.lobbys.values.sumOf { it.scriptTasks.values.filter { it is LuaTask }.size }
        })
    }
}