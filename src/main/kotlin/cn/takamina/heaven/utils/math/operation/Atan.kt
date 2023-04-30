package cn.takamina.heaven.utils.math.operation

import kotlin.math.atan

class Atan(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return atan(a())
    }


    override fun toString(): String {
        return "atan[$a]"
    }

    override fun unchangeable(): Boolean {
        return a.isUnchangeable()
    }
}