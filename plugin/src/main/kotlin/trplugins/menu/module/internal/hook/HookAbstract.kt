package trplugins.menu.module.internal.hook

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import taboolib.common.platform.function.console
import taboolib.common.util.unsafeLazy
import taboolib.module.lang.sendLang
import trplugins.menu.module.internal.script.Bindings
import trplugins.menu.module.internal.script.jexl.JexlAgent
import trplugins.menu.module.internal.script.js.JavaScriptAgent

/**
 * @author Arasple
 * @date 2021/1/26 22:02
 */
abstract class HookAbstract {

    open val name by lazy { getPluginName() }
    open val namespace by lazy { name.lowercase() }

    fun getFastName(): String? {
        return null
    }

    val plugin: Plugin? by unsafeLazy {
        Bukkit.getPluginManager().getPlugin(name)
    }

    open val isHooked by lazy {
        if (Bindings.exportHook) bindingScript()
        plugin != null && plugin!!.isEnabled
    }

    open fun getPluginName(): String {
        if (getFastName() != null) {
            return getFastName()!!
        }
        return javaClass.simpleName.substring(4)
    }

    fun checkHooked(): Boolean {
        return if (isHooked) true else false.also { reportAbuse() }
    }

    fun reportAbuse() {
        console().sendLang("Plugin-Dependency-Abuse", name)
    }

    open fun bindingScript() {
        JavaScriptAgent.putBinding(namespace, this)
        JexlAgent.putBinding(namespace, this)
    }

}