package me.arasple.mc.trmenu.modules.command.sub

import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.module.command.base.BaseSubCommand
import me.arasple.mc.trmenu.TrMenu
import me.arasple.mc.trmenu.modules.function.migrate.Migrate
import me.arasple.mc.trmenu.util.Paster
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

/**
 * @author Arasple
 * @date 2020/8/26 14:55
 */
class CommandDump : BaseSubCommand() {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>) {
        sender.sendMessage("§7Dumping information... please wait!")

        CompletableFuture.supplyAsync {
            Paster.paste(buildDump())
        }.whenComplete { result, _ ->
            sender.sendMessage("§aSuccessfully posted dump information at §3$result")
        }
    }

    private fun buildDump(): String {

        return buildString {
            append("TrMenu Dump Information (${Migrate.getExactDate()})\n\n")
            append("| Server OS: ${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}\n")
            append("| Server software: ${Bukkit.getServer().version} (${Bukkit.getServer().bukkitVersion})\n")
            append("| Java version: ${System.getProperty("java.version")}\n\n")
            append("| TabooLib: ${TabooLib.getVersion()}\n")
            append("| TrMenu: ${TrMenu.plugin.description.version}\n\n")
            append("Installed Plugins: \n")

            Bukkit.getPluginManager().plugins.sortedBy { it.name }.forEach {
                append("· ${it.name} - ${it.description.version}\n")
            }
        }
    }

}