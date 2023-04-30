package cn.takamina.heaven.utils.math.operation

import kotlin.math.sin

class Sin(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return sin(a())
    }

    override fun toString(): String {
        return "sin[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}