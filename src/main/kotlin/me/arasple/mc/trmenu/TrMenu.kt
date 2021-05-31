package me.arasple.mc.trmenu

import io.izzel.taboolib.Version
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.locale.TLocale
import me.arasple.mc.trmenu.modules.command.registerable.RegisterCommands
import me.arasple.mc.trmenu.modules.conf.MenuLoader
import me.arasple.mc.trmenu.modules.display.Menu
import me.arasple.mc.trmenu.modules.function.Shortcuts
import me.arasple.mc.trmenu.modules.function.hook.HookInstance
import me.arasple.mc.trmenu.util.Watchers
import me.clip.placeholderapi.PlaceholderAPIPlugin
import org.bukkit.Bukkit

/**
 * @author Arasple
 * @date 2020/2/26 10:05
 */
object TrMenu : Plugin() {

    @TInject("settings.yml", locale = "Options.Locale", migrate = true)
    lateinit var SETTINGS: TConfig
        private set

    override fun onLoad() {
        if (!SETTINGS.getBoolean("Options.Hide-Logo", false)) {
            printLogo()
        }
        TLocale.sendToConsole("PLUGIN.LOADING", Version.getBukkitVersion())
    }

    override fun onEnable() {
        SETTINGS.listener { onSettingsReload() }.also { onSettingsReload() }
        HookInstance.init()
        TLocale.sendToConsole("PLUGIN.LOADED", TrMenu.plugin.description.version)
        MenuLoader.loadMenus()
    }

    override fun onDisable() {
        Menu.getAllMenus().entries.forEach { it.value.forEach { menu -> menu.close(true) } }
        PlaceholderAPIPlugin.getInstance().localExpansionManager.findExpansionByIdentifier("trmenu").ifPresent {
            it.unregister()
        }
        Watchers.watcher.unregisterAll()
    }

    private fun onSettingsReload() {
        RegisterCommands.load()
        Shortcuts.load()
    }

    private fun printLogo() = arrayOf(
        "§8  ___________         _____                        ________",
        "§8    \\__    ___/______  /     \\   ____   ____  __ __  \\_____  \\",
        "§8    |    |  \\_  __ \\/  \\ /  \\_/ __ \\ /    \\|  |  \\  /  ____/",
        "§8    |    |   |  | \\/    Y    \\  ___/|   |  \\  |  / /       \\",
        "§8    |____|   |__|  \\____|__  /\\___  >___|  /____/  \\_______ \\",
        "§8    \\/     \\/     \\/                \\/",
        "                                                      "
    ).let {
        it.forEachIndexed { index, raw ->
            if (raw.isNotBlank()) {
                val line = raw.toCharArray()
                val width = (3..6).random()
                var randomIndex: Int
                do {
                    randomIndex = (2..line.size - width).random()
                } while (String(line.copyOfRange(randomIndex, randomIndex + width)).isBlank())
                val replace = String(line.copyOfRange(randomIndex, randomIndex + width))
                it[index] = String(line).replaceFirst(replace, "§${arrayOf('9', 'b', '3').random()}$replace§8")
            }
        }
        Bukkit.getConsoleSender().sendMessage(it)
    }
}