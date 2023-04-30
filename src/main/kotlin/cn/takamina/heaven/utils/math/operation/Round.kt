package cn.takamina.heaven.utils.math.operation

import java.math.BigDecimal

class Round(
    val a: () -> Double,
    val b: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return BigDecimal(a()).setScale(b().toInt()).toDouble()
    }


    override fun toString(): String {
        return "round[$a,$b]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable())) && ((b !is Operator) || (b.unchangeable()))
    }

    override fun simplify(): String {
        return if (unchangeable()) {
            invoke().toString()
        } else {
            return "round[${
                if (a is Operator) {
                    a.simplify()
                } else {
                    a.toString()
                }
            },${
                if (b is Operator) {
                    b.simplify().toInt()
                } else {
                    b().toInt().toString()
                }
            }]"
        }
    }
}