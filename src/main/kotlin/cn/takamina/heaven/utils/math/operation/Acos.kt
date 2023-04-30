package cn.takamina.heaven.utils.math.operation

import kotlin.math.acos

class Acos(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return acos(a())
    }

    override fun toString(): String {
        return "acos[$a]"
    }

    override fun unchangeable(): Boolean {
        return a.isUnchangeable()
    }

}