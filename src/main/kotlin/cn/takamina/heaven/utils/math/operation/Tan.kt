package cn.takamina.heaven.utils.math.operation

import kotlin.math.tan

class Tan(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return tan(a())
    }

    override fun toString(): String {
        return "tan[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}