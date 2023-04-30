package cn.takamina.heaven.utils.math.operation

import kotlin.math.asin

class Asin(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return asin(a())
    }

    override fun toString(): String {
        return "asin[$a]"
    }

    override fun unchangeable(): Boolean {
        return a.isUnchangeable()
    }
}