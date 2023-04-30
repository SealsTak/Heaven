package cn.takamina.heaven.utils.math.operation

import kotlin.math.max

class Max(
    val a: () -> Double,
    val b: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return max(a(), b())
    }

    override fun toString(): String {
        return "max[$a,$b]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable())) && ((b !is Operator) || (b.unchangeable()))
    }

    override fun simplify(): String {
        return if (unchangeable()) {
            invoke().toString()
        } else {
            return "max[${
                if (a is Operator) {
                    a.simplify()
                } else {
                    a.toString()
                }
            },${
                if (b is Operator) {
                    b.simplify()
                } else {
                    b.toString()
                }
            }]"
        }
    }
}