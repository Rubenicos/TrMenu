package trplugins.menu.util

import taboolib.common5.cint

/**
 * @author Arasple
 * @date 2021/1/31 11:53
 */
@JvmInline
value class EvalResult(val any: Any? = null) {

    fun asBoolean(def: Boolean = false): Boolean {
        return when (any) {
            is Boolean -> any
            is String -> Regexs.parseBoolean(any)
            else -> def || Regexs.parseBoolean(any.toString())
        }
    }

    fun asString(): String {
        return any.toString()
    }

    fun asInt(def: Int = 0): Int {
        return any?.cint ?: def
    }

    companion object {

        val TRUE = EvalResult(true)

        val FALSE = EvalResult(false)

    }

}
