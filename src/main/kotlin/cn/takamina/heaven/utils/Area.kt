package cn.takamina.heaven.utils

import org.bukkit.util.NumberConversions
import org.bukkit.util.Vector
import java.util.*


class Area private constructor(
    var minX: Double,
    var minY: Double,
    var minZ: Double,
    var maxX: Double,
    var maxY: Double,
    var maxZ: Double,
) {

    init {
        resize(minX, minY, minZ, maxX, maxY, maxZ)
    }

    fun resize(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Area {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(y1, "y1 not finite");
        NumberConversions.checkFinite(z1, "z1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(y2, "y2 not finite");
        NumberConversions.checkFinite(z2, "z2 not finite");
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
        return this
    }

    fun getWidthX(): Double {
        return maxX - minX
    }

    fun getWidthZ(): Double {
        return maxZ - minZ
    }

    fun getHeight(): Double {
        return maxY - minY
    }

    fun getVolume(): Double {
        return getHeight() * getWidthX() * getWidthZ()
    }

    fun getCenterX(): Double {
        return minX + getWidthX() * 0.5
    }

    fun getCenterY(): Double {
        return minY + getHeight() * 0.5
    }

    fun getCenterZ(): Double {
        return minZ + getWidthZ() * 0.5
    }

    fun getCenter(): Vector {
        return Vector(getCenterX(), getCenterY(), getCenterZ())
    }

    fun expand(
        negativeX: Double,
        negativeY: Double,
        negativeZ: Double,
        positiveX: Double,
        positiveY: Double,
        positiveZ: Double
    ): Area {
        return if (negativeX == 0.0 && negativeY == 0.0 && negativeZ == 0.0 && positiveX == 0.0 && positiveY == 0.0 && positiveZ == 0.0) {
            this
        } else {
            var newMinX = minX - negativeX
            var newMinY = minY - negativeY
            var newMinZ = minZ - negativeZ
            var newMaxX = maxX + positiveX
            var newMaxY = maxY + positiveY
            var newMaxZ = maxZ + positiveZ
            var centerZ: Double
            if (newMinX > newMaxX) {
                centerZ = getCenterX()
                if (newMaxX >= centerZ) {
                    newMinX = newMaxX
                } else if (newMinX <= centerZ) {
                    newMaxX = newMinX
                } else {
                    newMinX = centerZ
                    newMaxX = centerZ
                }
            }
            if (newMinY > newMaxY) {
                centerZ = getCenterY()
                if (newMaxY >= centerZ) {
                    newMinY = newMaxY
                } else if (newMinY <= centerZ) {
                    newMaxY = newMinY
                } else {
                    newMinY = centerZ
                    newMaxY = centerZ
                }
            }
            if (newMinZ > newMaxZ) {
                centerZ = getCenterZ()
                if (newMaxZ >= centerZ) {
                    newMinZ = newMaxZ
                } else if (newMinZ <= centerZ) {
                    newMaxZ = newMinZ
                } else {
                    newMinZ = centerZ
                    newMaxZ = centerZ
                }
            }
            resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
        }
    }

    fun union(posX: Double, posY: Double, posZ: Double): Area {
        val newMinX = Math.min(minX, posX)
        val newMinY = Math.min(minY, posY)
        val newMinZ = Math.min(minZ, posZ)
        val newMaxX = Math.max(maxX, posX)
        val newMaxY = Math.max(maxY, posY)
        val newMaxZ = Math.max(maxZ, posZ)
        return if (newMinX == minX && newMinY == minY && newMinZ == minZ && newMaxX == maxX && newMaxY == maxY && newMaxZ == maxZ) this else resize(
            newMinX,
            newMinY,
            newMinZ,
            newMaxX,
            newMaxY,
            newMaxZ
        )
    }

    /**
     * 是否重合
     */
    fun overlaps(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ
    }

    fun contains(x: Double, y: Double, z: Double): Boolean {
        return x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ
    }

    operator fun contains(position: Vector): Boolean {
        return this.contains(position.x, position.y, position.z)
    }

    private fun contains(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        return this.minX <= minX && this.maxX >= maxX && this.minY <= minY && this.maxY >= maxY && this.minZ <= minZ && this.maxZ >= maxZ
    }

    operator fun contains(other: Area): Boolean {
        return this.contains(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ)
    }

    override fun toString(): String {
        return "$minX $minY $minZ $maxX $maxY $maxZ"
    }

    companion object {
        fun parse(text: String): Area {
            val values = LinkedList(text.split(if (text.contains(',')) ',' else ' ').map { it.toDouble() })
            for (i in values.size until 6) {
                values.add(0.0)
            }
            return Area(values[0], values[1], values[2], values[3], values[4], values[5])
        }
    }
}