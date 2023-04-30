package cn.takamina.heaven.utils.math.operation

import kotlin.math.floor

class Floor(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return floor(a()).toInt().toDouble()
    }

    override fun toString(): String {
        return "floor[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}