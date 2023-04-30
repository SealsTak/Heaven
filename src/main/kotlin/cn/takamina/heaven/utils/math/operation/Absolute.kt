package cn.takamina.heaven.utils.math.operation

import kotlin.math.abs

class Absolute(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return abs(a())
    }

    override fun toString(): String {
        return "abs[$a]"
    }

    override fun unchangeable(): Boolean {
        return a.isUnchangeable()
    }
}