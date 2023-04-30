package cn.takamina.heaven.utils.math.operation

class Self(
    val value: () -> Double
) : Operator {

    override fun invoke(): Double {
        return value()
    }


    override fun toString(): String {
        return "${value().let { if (it < 0) "($it)" else it }}"
    }

    override fun unchangeable(): Boolean {
        return true
    }
}