package cn.takamina.heaven.utils.math.operation

import kotlin.math.ceil

class Ceil(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return ceil(a()).toInt().toDouble()
    }

    override fun toString(): String {
        return "ceil[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}