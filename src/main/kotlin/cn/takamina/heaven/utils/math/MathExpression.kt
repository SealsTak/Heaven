package cn.takamina.heaven.utils.math.operation

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Pattern
import kotlin.math.*

@Deprecated("Use MathFunction if the math function will be invoke time repeatedly")
object MathExpression {
    var SCALE: Int = 12

    val valueMap = mapOf<String, String>(
        "<π>" to Math.PI.toString(),
        "<PI>" to Math.PI.toString(),
        "<e>" to Math.E.toString()
    )

    fun preProcesss(exp: String): String {
        var express = exp
        valueMap.forEach { (k, v) ->
            express = express.replace(k, v)
        }
        express = express.replace("\\s".toRegex(), "")
        express = express.replace("()".toRegex(), "")
        express = if (express.startsWith("--")) express.substring(2) else express
        express = express.replace("--".toRegex(), "+")
        express = express.replace("\\+-".toRegex(), "-")
        return express
    }

    fun calculate(exp: String): String {
        var express = preProcesss(exp)

        if (express.matches("-{0,1}[0-9]+([.][0-9]+){0,1}".toRegex()))
            return express

        var newExpr = ""

        if (express.matches(".*([a-z]{3}\\[+?((.(?!\\[|,))*?)\\]+?).*".toRegex())) {
            val p = Pattern.compile("[a-z]{3}\\[+?((.(?!\\[|,))*?)\\]+?")
            val m = p.matcher(express)
            if (m.find()) {
                val temp = m.group()
                val operator = temp.substring(0, 3)
                val subExpr = temp.substring(4, temp.length - 1)
                newExpr = (express.substring(
                    0,
                    m.start()
                ) + oneVarCal(
                    calculate(
                        subExpr
                    ).toDouble(), operator
                ) + express.substring(m.end()))
            }
            return calculate(newExpr)
        }

        // 计算对数
        if (express.matches(".*[a-z]{3}\\[(?!.*\\[.*).*?,.*?].*".toRegex())) {
            val p = Pattern.compile("[a-z]{3}\\[(?!.*\\[.*).*?,.*?]")
            val m = p.matcher(express)
            if (m.find()) {
                val temp = m.group()
                val operator = temp.substring(0, 3)
                val subExpr = temp.substring(4, temp.length - 1)
                val `var` = subExpr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                `var`[0] = calculate(`var`[0])
                `var`[1] = calculate(`var`[1])
                newExpr = (express.substring(0, m.start()) + twoVarCal(
                    `var`[0].toDouble(),
                    `var`[1].toDouble(),
                    operator
                ) + express.substring(m.end()))
            }
            return calculate(newExpr)
        }
        // 去括号至无括号
        if (express.contains("(")) {
            val lIndex: Int = express.lastIndexOf("(")
            val rIndex: Int = express.indexOf(")", lIndex)
            val subExpr: String = express.substring(lIndex + 1, rIndex)
            newExpr =
                (express.substring(0, lIndex) + calculate(subExpr) //调用本身，计算括号中表达式结果
                        + express.substring(rIndex + 1))
            return calculate(newExpr)
        }

        // 计算阶乘
        if (express.contains("^")) {
            val p = Pattern.compile("[0-9]+([.][0-9]+){0,1}[\\^]-{0,1}[0-9]+([.][0-9]+){0,1}")
            val m = p.matcher(express)
            if (m.find()) {
                val temp = m.group()
                val a = temp.split("\\^".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                newExpr = (express.substring(0, m.start()) + twoVarCal(
                    java.lang.Double.valueOf(a[0]),
                    java.lang.Double.valueOf(a[1]),
                    temp[a[0].length].toString()
                ) + express.substring(m.end()))
            }
            return calculate(newExpr)
        }

        // 去乘除至无乘除
        if (express.contains("*") || express.contains("/")) {
            val p = Pattern.compile("[0-9]+([.][0-9]+){0,1}[*/]-{0,1}[0-9]+([.][0-9]+){0,1}")
            val m = p.matcher(express)
            if (m.find()) {
                val temp = m.group()
                val a = temp.split("[*/]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                newExpr = (express.substring(0, m.start()) + twoVarCal(
                    java.lang.Double.valueOf(a[0]),
                    java.lang.Double.valueOf(a[1]),
                    temp[a[0].length].toString()
                ) + express.substring(m.end()))
            }
            return calculate(newExpr)
        }

        // 计算取余
        if (express.contains("%")) {
            val p = Pattern.compile("[0-9]+([.][0-9]+){0,1}[\\%]-{0,1}[0-9]+([.][0-9]+){0,1}")
            val m = p.matcher(express)
            if (m.find()) {
                val temp = m.group()
                val a = temp.split("\\%".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                newExpr = (express.substring(0, m.start())
                        + twoVarCal(
                    java.lang.Double.valueOf(a[0]),
                    java.lang.Double.valueOf(a[1]),
                    temp[a[0].length].toString()
                ) + express.substring(m.end()))
            }
            return calculate(newExpr)
        }

        // 去加减至无加减
        if (express.contains("+") || express.contains("-")) {
            val p = Pattern.compile("-?[0-9]+([.][0-9]+)?[+-][0-9]+([.][0-9]+)?")
            val m = p.matcher(express)
            if (m.find()) {
                val temp = m.group()
                val a = temp.split("\\b[+-]".toRegex(), limit = 2).toTypedArray()
                newExpr = (express.substring(0, m.start()) + twoVarCal(
                    a[0].toDouble(),
                    a[1].toDouble(),
                    temp[a[0].length].toString()
                ) + express.substring(m.end()))
            }
            return calculate(newExpr)
        }

        throw NumberFormatException("Expression Error: $exp")
    }

    fun oneVarCal(a: Double, operator: String?): Double {
        return round(
            when (operator) {
                "sin" -> sin(a)
                "cos" -> cos(a)
                "tan" -> tan(a)
                "asi" -> asin(a)
                "aco" -> acos(a)
                "ata" -> atan(a)
                "abs" -> abs(a)
                else -> 0.0
            }, SCALE
        )
    }

    fun twoVarCal(a: Double, b: Double, operator: String?): Double {
        return when (operator) {
            "+" -> BigDecimal(a).add(BigDecimal(b)).setScale(SCALE, RoundingMode.HALF_UP).toDouble()
            "-" -> BigDecimal(a).subtract(BigDecimal(b)).setScale(SCALE, RoundingMode.HALF_UP).toDouble()
            "*" -> BigDecimal(a).multiply(BigDecimal(b)).setScale(SCALE, RoundingMode.HALF_UP).toDouble()
            "/" -> BigDecimal(a).divide(BigDecimal(b), BigDecimal.ROUND_HALF_UP).setScale(SCALE, RoundingMode.HALF_UP)
                .toDouble()

            "^" -> a.pow(b)
            "%" -> a % b
            "log" -> {
                if ((a == 0.0) || (a == 1.0) || (b == 0.0) || (b == 1.0))
                    0.0
                else
                    ln(b) / ln(a)
            }

            "rou" -> round(a, b.toInt())
            else -> 0.0
        }
    }

    fun round(a: Double, scale: Int): Double {
        require(scale >= 0) { "The   scale   must   be   a   positive   integer   or   zero" }
        val vb = BigDecimal(a)
        val one = BigDecimal("1")
        return vb.divide(one, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}