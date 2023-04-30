package cn.takamina.heaven.utils.math.operation

class Degrees(
    val a: () -> Double,
) : Operator {

    override fun invoke(): Double {
        return Math.toDegrees(a())
    }

    override fun toString(): String {
        return "degrees[$a]"
    }

    override fun unchangeable(): Boolean {
        return ((a !is Operator) || (a.unchangeable()))
    }
}