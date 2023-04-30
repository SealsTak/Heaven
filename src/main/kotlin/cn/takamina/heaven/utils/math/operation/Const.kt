package cn.takamina.heaven.utils.math.operation

class Const(
    val a: Double,
) : Operator {

    override fun invoke(): Double {
        return a
    }

    override fun toString(): String {
        return "$a"
    }

    override fun unchangeable(): Boolean {
        return true
    }
}