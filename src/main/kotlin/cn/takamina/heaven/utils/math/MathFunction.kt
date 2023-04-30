package cn.takamina.heaven.utils.math.operation

import java.util.*
import java.util.regex.Pattern

/**
 * 解析数学表达式
 * @author Takamina
 */
class MathFunction() {
    val NUM: String = "([-+]{0,1}\\d+(\\.\\d+)?)"
    val operators = LinkedList<Operator>()
    var arguments = HashMap<String, () -> Double>()

    private fun preProcesss(exp: String): String {
        var expression = exp
        expression = expression.replace("<PI>", Math.PI.toString())
        expression = expression.replace("<π>", Math.PI.toString())
        expression = expression.replace("<e>", Math.E.toString())
        expression = expression.replace("\\s".toRegex(), "")
        expression = expression.replace("()".toRegex(), "")
        expression = if (expression.startsWith("--")) expression.substring(2) else expression
        expression = expression.replace("--".toRegex(), "+")
        expression = expression.replace("\\+-".toRegex(), "-")
        return expression
    }

    fun analyze(exp: String, fastHandle: Boolean = true): Operator {
        var expression = preProcesss(exp)

        if (expression.matches(NUM.toRegex())) return Operator.self { expression.toDouble() }

        var pattern = Pattern.compile("[a-z]+\\([^()]*\\)")
        var matcher = pattern.matcher(expression)
        while (matcher.find()) {
            var subExp = matcher.group()
            val opr = subExp.slice(0 until subExp.indexOf('('))
            val newExp = subExp.slice(opr.length + 1 until subExp.length - 1)
            val operator = if (newExp.contains(',')) {
                val parts = subExp.slice(opr.length + 1 until subExp.length - 1).split(",")
                when (opr.lowercase()) {
                    "log" -> Operator.log(analyze(parts[0]), analyze(parts[1]))
                    "rou", "round" -> Operator.round(analyze(parts[0]), analyze(parts[1]))
                    "ran", "random" -> Operator.random(analyze(parts[0]), analyze(parts[1]))
                    "max" -> Operator.max(analyze(parts[0]), analyze(parts[1]))
                    "min" -> Operator.min(analyze(parts[0]), analyze(parts[1]))
                    else -> throw NumberFormatException(opr)
                }
            } else {
                when (opr.lowercase()) {
                    "sin" -> Operator.sin(analyze(newExp))
                    "cos" -> Operator.cos(analyze(newExp))
                    "tan" -> Operator.tan(analyze(newExp))
                    "asi", "asin" -> Operator.asin(analyze(newExp))
                    "aco", "acos" -> Operator.acos(analyze(newExp))
                    "ata", "atan" -> Operator.atan(analyze(newExp))
                    "abs" -> Operator.absolute(analyze(newExp))
                    "deg", "degrees" -> Operator.degrees(analyze(newExp))
                    "rad", "radians" -> Operator.radians(analyze(newExp))
                    "ceil" -> Operator.ceil(analyze(newExp))
                    "floor" -> Operator.floor(analyze(newExp))
                    else -> throw NumberFormatException("")
                }
            }
            expression = expression.replace(subExp, "<${operators.size}>")
            matcher = pattern.matcher(expression)
            operators.add(operator)
        }

        pattern = Pattern.compile("\\([^()\\[\\]]+\\)")
        matcher = pattern.matcher(expression)
        while (matcher.find()) {
            var subExp = matcher.group()
            val operator = analyze(subExp.slice(1 until subExp.length - 1))
            expression = expression.replace(subExp, "<${operators.size}>")
            matcher = pattern.matcher(expression)
            operators.add(operator)
        }

        pattern =
            Pattern.compile("(<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)\\^(<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)")
        matcher = pattern.matcher(expression)
        while (matcher.find()) {
            val subExp = matcher.group()
            expression = expression.replace(subExp, "<${operators.size}>")
            matcher = pattern.matcher(expression)
            val parts = subExp.split("(?!<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)\\^".toRegex(), 2);
            when (subExp[parts[0].length]) {
                '^' -> {
                    operators.add(
                        Operator.factorial(
                            placeholderReplacement(parts[0]),
                            placeholderReplacement(parts[1])
                        )
                    )
                }
            }
        }

        pattern =
            Pattern.compile("(<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)[*/](<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)")
        matcher = pattern.matcher(expression)
        while (matcher.find()) {
            val subExp = matcher.group()
            expression = expression.replace(subExp, "<${operators.size}>")
            matcher = pattern.matcher(expression)
            val parts = subExp.split("(?!<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)[*/]".toRegex(), 2)
            when (subExp[parts[0].length]) {
                '*' -> {
                    operators.add(
                        Operator.multiply(
                            placeholderReplacement(parts[0]),
                            placeholderReplacement(parts[1]),
                            fastHandle
                        )
                    )
                }

                '/' -> {
                    operators.add(
                        Operator.divide(
                            placeholderReplacement(parts[0]),
                            placeholderReplacement(parts[1]),
                            fastHandle
                        )
                    )
                }
            }
        }

        pattern =
            Pattern.compile("(<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)%(<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)")
        matcher = pattern.matcher(expression)
        while (matcher.find()) {
            val subExp = matcher.group()
            expression = expression.replace(subExp, "<${operators.size}>")
            matcher = pattern.matcher(expression)
            val parts = subExp.split("(?!<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)%".toRegex(), 2)
            when (subExp[parts[0].length]) {
                '%' -> {
                    operators.add(Operator.mod(placeholderReplacement(parts[0]), placeholderReplacement(parts[1])))
                }
            }
        }

        pattern =
            Pattern.compile("(<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)[+-](<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)")
        matcher = pattern.matcher(expression)
        while (matcher.find()) {
            val subExp = matcher.group()
            expression = expression.replace(subExp, "<${operators.size}>")
            matcher = pattern.matcher(expression)
            val parts = subExp.split("(?!<[a-zA-Z][0-9a-zA-Z]*>|<\\d+>|[0-9]+(\\.[0-9]+)?)[+-]".toRegex(), 2)
            when (subExp[parts[0].length]) {
                '+' -> {
                    operators.add(Operator.add(placeholderReplacement(parts[0]), placeholderReplacement(parts[1])))
                }

                '-' -> {
                    operators.add(Operator.subtract(placeholderReplacement(parts[0]), placeholderReplacement(parts[1])))
                }
            }
        }

        return placeholderReplacement(expression)
    }

    private fun placeholderReplacement(str: String): Operator {
        return if (str.matches("<\\d+>".toRegex()))
            operators[str.slice(1 until str.length - 1).toInt()]
        else if (str.matches("<[a-zA-Z][0-9a-zA-Z_.]*>".toRegex())) {
            val arg = str.slice(1 until str.length - 1)
            Operator.papi(arg) { arguments[arg]?.invoke() ?: 0.0 }
        } else
            Operator.const(kotlin.runCatching { str.toDouble() }.getOrDefault(0.0))
    }

    companion object {
        fun create(run: MathFunction.() -> Unit): MathFunction {
            return MathFunction().also(run)
        }
    }
}