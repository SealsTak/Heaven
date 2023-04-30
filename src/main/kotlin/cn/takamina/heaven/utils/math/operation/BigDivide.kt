package cn.takamina.heaven.utils.math.operation

import java.math.BigDecimal
import java.math.RoundingMode

class BigDivide(
    val a: () -> Double,
    val b: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return (BigDecimal(a()) / BigDecimal(b())).setScale(8, RoundingMode.HALF_UP).toDouble()
    }

    override fun toString(): String {
        return "$a/$b"
    }

    override fun unchangeable(): Boolean {
        return a.isUnchangeable() && b.isUnchangeable()
    }

    override fun simplify(): String {
        return if (unchangeable()) {
            invoke().toString()
        } else {
            return "${
                if (a is Operator) {
                    a.simplify()
                } else {
                    a.toString()
                }
            }/${
                if (b is Operator) {
                    b.simplify()
                } else {
                    b.toString()
                }
            }"
        }
    }
}