package cn.takamina.heaven.utils

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import io.github.config4k.ClassContainer
import io.github.config4k.CustomType
import io.github.config4k.registerCustomType
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.common.env.RuntimeDependency

@RuntimeDependency("io.github.config4k:config4k:0.5.0")
object HoconUtil {

    val LocationParser = object : CustomType {
        override fun parse(clazz: ClassContainer, config: Config, name: String): Any? {
            return if (clazz.mapperClass == Location::class && config.hasPath(name)) {
                val config = config.getConfig(name)
                val x = config.getValue("x").unwrapped().toString().toDouble()
                val y = config.getValue("y").unwrapped().toString().toDouble()
                val z = config.getValue("z").unwrapped().toString().toDouble()
                var world: String = ""
                var yaw = 0f
                var pitch = 0f
                if (config.hasPath("world")) {
                    world = config.getValue("world").unwrapped().toString()
                }
                if (config.hasPath("yaw")) {
                    yaw = config.getValue("yaw").unwrapped().toString().toFloat()
                }
                if (config.hasPath("pitch")) {
                    pitch = config.getValue("pitch").unwrapped().toString().toFloat()
                }
                Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
            } else null
        }

        override fun testParse(clazz: ClassContainer): Boolean {
            return clazz.mapperClass == Location::class
        }

        override fun testToConfig(obj: Any): Boolean {
            return obj is Location
        }

        override fun toConfig(obj: Any, name: String): Config {
            if (obj is Location) {
                var loc = ConfigFactory.empty()
                loc = loc.withValue("world", ConfigValueFactory.fromAnyRef((obj.world?.name ?: "")))
                loc = loc.withValue("x", ConfigValueFactory.fromAnyRef(obj.x))
                loc = loc.withValue("y", ConfigValueFactory.fromAnyRef(obj.y))
                loc = loc.withValue("z", ConfigValueFactory.fromAnyRef(obj.z))
                loc = loc.withValue("yaw", ConfigValueFactory.fromAnyRef(obj.yaw))
                loc = loc.withValue("pitch", ConfigValueFactory.fromAnyRef(obj.pitch))
                return ConfigFactory.empty().withValue(name, loc.root())
            }
            return ConfigFactory.empty()
        }
    }

    init {
        registerCustomType(LocationParser)
    }
}