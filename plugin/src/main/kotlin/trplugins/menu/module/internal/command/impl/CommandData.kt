package trplugins.menu.module.internal.command.impl

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.player
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.common.platform.command.suggestUncheck
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.util.asList
import taboolib.platform.util.sendLang
import trplugins.menu.module.internal.command.CommandExpression
import trplugins.menu.module.internal.data.Metadata

/**
 * @author 嘿鹰
 * @date 2025/8/19 17:39
 */
object CommandData : CommandExpression {

    // trm data [modify] [dataType] [dataName] [value] [player]
    override val command = subCommand {
        dynamic("modify") {
            suggest { listOf("add", "remove", "set", "get") }
            dynamic("dataType") {
                suggest {
                    listOf("data", "meta", "global")
                }
                dynamic("dataName") {
                    suggestUncheck {
                        val dataType = Metadata.DataType.valueOf(ctx["dataType"].uppercase())
                        getSuggestions(sender, dataType)
                    }
                    exec<Player> {
                        val modifyType = Metadata.ModifyType.valueOf(ctx["modify"].uppercase())
                        if (modifyType == Metadata.ModifyType.ADD || modifyType == Metadata.ModifyType.SET) {
                            sender.sendLang("Command-Data-Invalid-Modify")
                            return@exec
                        }
                        val dataType = Metadata.DataType.valueOf(ctx["dataType"].uppercase())
                        val dataName = ctx["dataName"]
                        Metadata.modifyData(sender, modifyType, dataType, dataName, "", sender)
                    }
                    dynamic("value") {
                        suggestUncheck {
                            val modifyType = Metadata.ModifyType.valueOf(ctx["modify"].uppercase())
                            if (modifyType == Metadata.ModifyType.GET || modifyType == Metadata.ModifyType.REMOVE) {
                                onlinePlayers().map { it.name }
                            } else emptyList()
                        }
                        exec<CommandSender> {
                            val modifyType = Metadata.ModifyType.valueOf(ctx["modify"].uppercase())
                            val dataType = Metadata.DataType.valueOf(ctx["dataType"].uppercase())
                            val dataName = ctx["dataName"]
                            if (modifyType == Metadata.ModifyType.GET || modifyType == Metadata.ModifyType.REMOVE) {
                                val player = ctx.player("value").cast<Player>()
                                Metadata.modifyData(player, modifyType, dataType, dataName, "", sender)
                            } else {
                                if (sender is Player) {
                                    val value = ctx["value"]
                                    Metadata.modifyData(sender as Player, modifyType, dataType, dataName, value, sender)
                                }
                            }
                        }
                        player("player", optional = true) {
                            exec<CommandSender> {
                                val player = ctx.player("player").cast<Player>()
                                val modifyType = Metadata.ModifyType.valueOf(ctx["modify"].uppercase())
                                val dataType = Metadata.DataType.valueOf(ctx["dataType"].uppercase())
                                val dataName = ctx["dataName"]
                                val value = ctx["value"]
                                Metadata.modifyData(player, modifyType, dataType, dataName, value, sender)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSuggestions(sender: ProxyCommandSender, dataType: Metadata.DataType): List<String> {
        return if (dataType == Metadata.DataType.GLOBAL) {
            Metadata.getGlobalDataKeys().asList()
        } else {
            if (sender is ProxyPlayer) {
                if (dataType == Metadata.DataType.DATA) {
                    Metadata.getData(sender).data.keys.asList()
                } else {
                    Metadata.getMeta(sender).data.keys.asList()
                }
            } else emptyList()
        }
    }


}