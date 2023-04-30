package cn.takamina.heaven.utils.math.operation

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ln
import kotlin.math.pow

/**
 * 数学函数
 * @author Takamina
 */
interface Operator : () -> Double {
    fun unchangeable(): Boolean
    fun (() -> Double).isUnchangeable(): Boolean {
        return ((this !is Operator) || (this.unchangeable()))
    }

    fun simplify(): String {
        return if (unchangeable()) {
            invoke().toString()
        } else {
            toString()
        }
    }

    companion object {
        fun absolute(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.abs(a()))
            }
            return Absolute(a)
        }

        fun acos(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.acos(a()))
            }
            return Acos(a)
        }

        fun add(a: () -> Double, b: () -> Double): Operator {
            var a: () -> Double = a
            var b: () -> Double = b
            if (b is Const) {
                val c = b
                b = a
                a = c
            }
            if (a is Const) {
                if (b is Const) {
                    return const(a() + b())
                } else if (b is Add) {
                    if (b.a is Const) {
                        return add(const(b.a() + a.a), b.b)
                    } else if (b.b is Const) {
                        return add(const(b.b() + a.a), b.a)
                    }
                }
            }
            return Add(a, b)
        }

        fun asin(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.asin(a()))
            }
            return Asin(a)
        }

        fun atan(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.atan(a()))
            }
            return Atan(a)
        }

        fun cos(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.cos(a()))
            }
            return Cos(a)
        }

        fun divide(a: () -> Double, b: () -> Double, fastHandle: Boolean = true): Operator {
            if (a is Const && b is Const) {
                return if (fastHandle) const(a() / b()) else const(
                    (BigDecimal(a()) / BigDecimal(b())).setScale(
                        8,
                        RoundingMode.HALF_UP
                    ).toDouble()
                )
            }
            return if (fastHandle) Divide(a, b) else BigDivide(a, b)
        }

        fun factorial(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return const(a().pow(b()))
            }
            return Factorial(a, b)
        }

        fun log(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return const(ln(b()) / ln(a()))
            }
            return Log(a, b)
        }

        fun mod(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return const(a() % b().toInt())
            }
            return Mod(a, b)
        }

        fun multiply(a: () -> Double, b: () -> Double, fastHandle: Boolean = true): Operator {
            if (a is Const && b is Const) {
                return if (fastHandle) const(a() * b()) else const(
                    (BigDecimal(a()) * BigDecimal(b())).setScale(
                        8,
                        RoundingMode.HALF_UP
                    ).toDouble()
                )
            }
            return if (fastHandle) Multiply(a, b) else BigMultiply(a, b)
        }

        fun round(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return const(BigDecimal(a()).setScale(b().toInt()).toDouble())
            }
            return Round(a, b)
        }

        fun self(a: () -> Double): Operator {
            return Self(a)
        }

        fun sin(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.sin(a()))
            }
            return Sin(a)
        }

        fun subtract(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return const(a() - b())
            }
            return Subtract(a, b)
        }

        fun tan(a: () -> Double): Operator {
            if (a is Const) {
                return const(kotlin.math.tan(a()))
            }
            return Tan(a)
        }

        fun degrees(a: () -> Double): Operator {
            if (a is Const) {
                return const(Math.toDegrees(a()))
            }
            return Degrees(a)
        }

        fun radians(a: () -> Double): Operator {
            if (a is Const) {
                return const(Math.toDegrees(a()))
            }
            return Radians(a)
        }

        fun const(a: Double): Operator {
            return Const(a)
        }

        fun random(a: () -> Double, b: () -> Double): Operator {
            return Random(a, b)
        }

        fun ceil(a: () -> Double): Operator {
            if (a is Const) {
                return Const(kotlin.math.ceil(a()).toInt().toDouble())
            }
            return Ceil(a)
        }

        fun floor(a: () -> Double): Operator {
            if (a is Const) {
                return Const(kotlin.math.floor(a()).toInt().toDouble())
            }
            return Floor(a)
        }

        fun max(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return Const(kotlin.math.max(a(), b()))
            }
            return Max(a, b)
        }

        fun min(a: () -> Double, b: () -> Double): Operator {
            if (a is Const && b is Const) {
                return Const(kotlin.math.min(a(), b()))
            }
            return Min(a, b)
        }

        fun papi(name: String, value: () -> Double): Operator {
            return Papi(name, value)
        }
    }
}