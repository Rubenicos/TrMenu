package trplugins.menu.module.conf.prop

import taboolib.common.platform.function.console
import taboolib.common.util.replaceWithOrder
import trplugins.menu.module.display.icon.Icon
import trplugins.menu.module.display.layout.MenuLayout

/**
 * @author Arasple
 * @date 2021/1/25 10:12
 */
class SerialzeResult(
    val type: Type,
    var state: State = State.SUCCESS,
    val errors: MutableList<Throwable> = mutableListOf(),
    var result: Any? = null
) {

    fun succeed(): Boolean {
        return state == State.SUCCESS && result != null
    }

    fun submitError(error: Throwable) {
        errors.add(error)
    }

    fun submitError(error: SerializeError, vararg args: Any) {
        state = State.FAILED
        errors.add(RuntimeException(SerializeError.formatInfo(error).replaceWithOrder(*args)))
    }

    fun submitErrors(result: SerialzeResult): Boolean {
        return errors.addAll(result.errors)
    }

    fun printStackTrace() {
        errors.forEach { printStackTrace(it) }
    }

    fun printStackTrace(t: Throwable) {
        printStackTrace(t, "    §8")
    }

    fun printStackTrace(t: Throwable, prefix: String) {
        console().sendMessage(prefix + t)

        t.stackTrace.forEach { console().sendMessage("$prefix\tat $it") }

        val cause = t.cause
        if (cause != null) {
            console().sendMessage(prefix + "Caused by:")
            printStackTrace(cause)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun asIcons(): Set<Icon> {
        return (result as List<Icon>).toSet()
    }

    fun asLayout(): MenuLayout {
        return result as MenuLayout
    }

    enum class State {

        SUCCESS,
        FAILED,
        IGNORE

    }

    enum class Type {

        MENU,
        MENU_SETTING,
        MENU_LAYOUT,
        ICON,

    }

}