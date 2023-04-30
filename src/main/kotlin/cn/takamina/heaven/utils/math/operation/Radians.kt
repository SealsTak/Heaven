package cn.takamina.heaven.utils.math.operation

class Radians(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return Math.toRadians(a())
    }

    override fun toString(): String {
        return "radians[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}