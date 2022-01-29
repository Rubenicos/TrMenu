package trplugins.menu.api.action.impl.func

import trplugins.menu.api.action.base.AbstractAction
import trplugins.menu.api.action.base.ActionOption
import trplugins.menu.util.bukkit.ItemHelper
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import taboolib.common.platform.function.adaptPlayer
import taboolib.library.xseries.XEnchantment
import trplugins.menu.api.action.InternalAction
import trplugins.menu.api.action.base.ActionDesc

/**
 * @author Rubenicos
 * @date 2021/11/09 17:46
 */
class ActionEnchantItem(content: String, option: ActionOption) : InternalAction(content, option) {

    override fun onExecute(player: Player, placeholderPlayer: Player) {
        parseContentSplited(adaptPlayer(placeholderPlayer), ";").forEach {
            val split = it.split(",", " ", limit = 4).toTypedArray()
            if (split.size >= 3) {
                val l = split[2].split("-").toTypedArray()
                val level = if (l.size > 1) ((l[0].toIntOrNull()?: 0)..(l[1].toIntOrNull()?: 0)).random() else l[0].toIntOrNull()?: 0
                if (level > 0) {
                    val enchant = XEnchantment.matchXEnchantment(split[1])
                    if (enchant.isPresent) {
                        enchant(ItemHelper.fromPlayerInv(player.inventory, split[0]), enchant.get().parseEnchantment(), level)
                    }
                }
            }
        }
    }

    companion object : ActionDesc {

        override val name = "enchant(-)?items?".toRegex()

        override val parser: (Any, ActionOption) -> AbstractAction = { value, option ->
            ActionEnchantItem(value.toString(), option)
        }

        fun enchant(any: Any?, enchant: Enchantment?, level: Int) {
            if (any is Array<*>) {
                any.forEach { enchantItem(it as ItemStack?, enchant, level) }
            } else if (any is ItemStack) enchantItem(any, enchant, level)
        }

        fun enchantItem(item: ItemStack?, enchant: Enchantment?, level: Int) {
            if (item != null && enchant != null) {
                if (item.type == Material.BOOK) {
                    item.type = Material.ENCHANTED_BOOK
                }
                if (item.hasItemMeta()) {
                    val meta = item.itemMeta!!
                    if (meta is EnchantmentStorageMeta) {
                        meta.addStoredEnchant(enchant, level, true)
                    } else {
                        meta.addEnchant(enchant, level, true)
                    }
                    item.itemMeta = meta
                } else {
                    item.addUnsafeEnchantment(enchant, level)
                }
            }
        }
    }
}