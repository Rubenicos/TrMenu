package me.arasple.mc.trmenu.modules.command.sub

import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.internal.apache.lang3.ArrayUtils
import io.izzel.taboolib.kotlin.Indexed
import io.izzel.taboolib.loader.PluginBoot
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import me.arasple.mc.trmenu.TrMenu
import me.arasple.mc.trmenu.api.Extends.getArguments
import me.arasple.mc.trmenu.api.Extends.getMenuSession
import me.arasple.mc.trmenu.api.Extends.getMetas
import me.arasple.mc.trmenu.api.Extends.sendLocale
import me.arasple.mc.trmenu.api.TrMenuAPI
import me.arasple.mc.trmenu.modules.display.Menu
import me.arasple.mc.trmenu.modules.display.item.property.Mat
import me.arasple.mc.trmenu.modules.function.WebData
import me.arasple.mc.trmenu.modules.function.script.Scripts
import me.arasple.mc.trmenu.modules.function.script.expression.Expressions
import me.arasple.mc.trmenu.modules.service.metrics.MetricsHandler
import me.arasple.mc.trmenu.util.Msger
import me.arasple.mc.trmenu.util.Skulls
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author Arasple
 * @date 2020/3/7 12:36
 */
class CommandDebug : BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("Type", false) {
            listOf(
                "info",
                "player",
                "menu",
                "msgreplace",
                "skulls",
                "expressions",
                "reset",
                "parseExpression",
                "parseMat"
            )
        }
    )

    val description: FileConfiguration = PluginBoot.getPluginFile().fileConfiguration

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>) {
        if (args.isEmpty()) {
            debugSender(sender)
        } else {
            val content = ArrayUtils.remove(args, 0).joinToString(" ")
            when (args[0].toLowerCase()) {
                "info" -> printInfo(sender)
                "skulls" -> printSkulls(sender)
                "expressions" -> printExpressions(sender)
                "player" -> if (args.size > 1) printPlayer(sender, Bukkit.getPlayerExact(args[1]))
                "menu" -> if (args.size > 1) printMenu(sender, TrMenuAPI.getMenuById(args[1]))
                "reset" -> {
                    Skulls.CACHED_SKULLS.clear()
                    Skulls.CACHED_PLAYER_TEXTURE.clear()
                    WebData.CACHED_WEB_DATA.clear()
                }
                "msgreplace" -> {
                    if (sender is Player) {
                        val message = Indexed.join(args, 1)
                        sender.sendMessage(
                            Msger.replace(sender, message)
                        )
                    }
                }
                "parseexpression" -> {
                    val parsed = Expressions.parse(content)
                    val translated = Scripts.translate(parsed.first)
                    sender.sendLocale("DEBUG.EXPRESSION", content, parsed)
                    sender.sendLocale("DEBUG.PRE-COMPILE-SCRIPT", parsed, translated)
                }
                "parsemat" -> {
                    if (sender is Player) {
                        CronusUtils.addItem(sender, Mat.createMat(content).createItem(sender))
                    }
                }
            }
        }
    }

    private fun printSkulls(sender: CommandSender) {
        sender.sendMessage(
            arrayOf(
                "§3§l「§8--------------------------------------------------§3§l」",
                "",
                "§cCached_Skulls: §4${Skulls.CACHED_SKULLS.keys}",
                "§cCached_PlayerTextures: §4${Skulls.CACHED_PLAYER_TEXTURE}",
                "",
                "§3§l「§8--------------------------------------------------§3§l」"
            )
        )

        if (sender is Player) {
            Skulls.CACHED_SKULLS.values.forEach {
                CronusUtils.addItem(sender, it)
            }
        }
    }

    private fun printExpressions(sender: CommandSender) {
        sender.sendMessage(
            arrayOf(
                "§3§l「§8--------------------------------------------------§3§l」",
                "",
                "§8${
                    Expressions.CACHED_PARSED.map {
                        "\n§8${it.key} §3-> §f${it.value}"
                    }
                }",
                "",
                "§3§l「§8--------------------------------------------------§3§l」"
            )
        )
    }

    private fun debugSender(sender: CommandSender) {
        if (sender is Player) {
            if (sender.hasMetadata("TrMenu:Debug")) {
                sender.removeMetadata("TrMenu:Debug", TrMenu.plugin)
                sender.sendLocale("COMMANDS.DEBUG.OFF")
            } else {
                sender.setMetadata("TrMenu:Debug", FixedMetadataValue(TrMenu.plugin, ""))
                sender.sendLocale("COMMANDS.DEBUG.ON")
            }
        } else sender.sendLocale("COMMANDS.DEBUG.${if (Msger.debug()) "ON" else "OFF"}")
    }

    private fun printPlayer(sender: CommandSender, player: Player?) {
        if (player != null) {
            val session = player.getMenuSession()
            sender.sendMessage(
                arrayOf(
                    "§3§l「§8--------------------------------------------------§3§l」",
                    "",
                    "§eCurrentMenu: §6${session.menu?.id}",
                    "§eCurrentPage: §6${session.page}",
                    "§eArguments: §6${player.getArguments().joinToString(", ", "{", "}")}",
                    "§eMetas: §6${player.getMetas()}",
                    "§eInternalFunctions: §6${session.menu?.settings?.functions?.internalFunctions?.map { it.id }}",
                    "",
                    "§3§l「§8--------------------------------------------------§3§l」"
                )
            )
        }
    }


    private fun printMenu(sender: CommandSender, menu: Menu?) {
        if (menu != null) {
            sender.sendMessage(
                arrayOf(
                    "§3§l「§8--------------------------------------------------§3§l」",
                    "",
                    "§aTitle: §6${menu.settings.title.titles} / ${menu.settings.title.update}",
                    "§aViewers: §6${menu.viewers.map { it.name }}",
                    "§aBindings: §6${menu.settings.bindings.boundCommands.joinToString(",")}",
                    "§aIcons: §6${menu.icons.size}",
                    "§aOpenEvent: §6${menu.settings.events.openEvent}",
                    "§aCloseEvent: §6${menu.settings.events.closeEvent}",
                    "",
                    "§3§l「§8--------------------------------------------------§3§l」"
                )
            )
        }
    }

    private fun printInfo(sender: CommandSender) {
        sender.sendMessage(
            arrayOf(
                "§3§l「§8--------------------------------------------------§3§l」",
                "",
                "§2Server: §6${Bukkit.getServer().name}",
                "§2Total Menus: §6"
            )
        )
        Menu.getAllMenus()
            .forEach { if (it.value.isNotEmpty()) sender.sendMessage("§r  §8▪ ${it.key}§8: §7${it.value.size}") }
        sender.sendMessage(
            arrayOf(
                "§2Cached WebDatas: §6${WebData.CACHED_WEB_DATA.size}",
                "§2Cached Skulls: §6${Skulls.CACHED_SKULLS.size}",
                "§2Cached Expressions: §6${Expressions.CACHED_PARSED.size}",
                "§2Running Tasks: §6${
                    Bukkit.getScheduler().activeWorkers.filter { it.owner === TrMenu.plugin }
                        .count() + Bukkit.getScheduler().pendingTasks.filter { it.owner === TrMenu.plugin }.count()
                }",
                "§2bStats: §3${MetricsHandler.B_STATS?.isEnabled}",
                "§2cStats: §3${MetricsHandler.C_STATS?.isEnabled}",
                "§2TabooLib: §f${description.getString("lib-version")}",
                "",
                "§3§l「§8--------------------------------------------------§3§l」"
            )
        )

        println(
            "Actived: " + Bukkit
                .getScheduler()
                .activeWorkers
                .filter { it.owner === TrMenu.plugin }
                .count()
        )

        println(
            "Pending: " + Bukkit
                .getScheduler()
                .pendingTasks
                .filter { it.owner === TrMenu.plugin }
        )
    }

}