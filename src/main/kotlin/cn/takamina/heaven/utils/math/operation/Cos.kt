package cn.takamina.heaven.utils.math.operation

import kotlin.math.cos

class Cos(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return cos(a())
    }

    override fun toString(): String {
        return "cos[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}