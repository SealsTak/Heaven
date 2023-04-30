package cn.takamina.heaven.utils.math.operation

import java.util.Random
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Random(
    val a: () -> Double,
    val b: () -> Double,
) : Operator {

    override fun invoke(): Double {
        val a = a()
        val b = b()
        val up = floor(max(a, b)).toInt()
        val down = ceil(min(a, b)).toInt()
        return down + Random().nextInt(max(1, up - down)) + 0.0
    }


    override fun toString(): String {
        return "random[$a,$b]"
    }

    override fun unchangeable(): Boolean {
        return false
    }
}