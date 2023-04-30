package cn.takamina.heaven.utils.math.operation

class Papi(
    val name: String,
    val value: () -> Double
) : Operator {

    override fun invoke(): Double {
        return value()
    }

    override fun toString(): String {
        return name
    }

    override fun unchangeable(): Boolean {
        return false
    }
}