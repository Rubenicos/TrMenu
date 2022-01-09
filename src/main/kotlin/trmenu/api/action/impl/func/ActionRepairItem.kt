package trmenu.api.action.impl.func

import trmenu.api.action.base.AbstractAction
import trmenu.api.action.base.ActionOption
import trmenu.util.bukkit.ItemHelper
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import trmenu.api.action.base.ActionDesc

/**
 * @author Rubenicos
 * @date 2021/11/09 17:46
 */
class ActionRepairItem(content: String, option: ActionOption) : AbstractAction(content, option) {

    override fun onExecute(player: Player, placeholderPlayer: Player) {
        parseContentSplited(placeholderPlayer, ";").forEach {
            repair(ItemHelper.fromPlayerInv(player.inventory, it))
        }
        player.updateInventory()
    }

    companion object : ActionDesc {

        override val name = "repair(-)?items?".toRegex()

        override val parser: (Any, ActionOption) -> AbstractAction = { value, option ->
            ActionRepairItem(value.toString(), option)
        }

        private fun repair(any: Any?) {
            if (any is Array<*>) {
                any.forEach { repairItem(it as ItemStack?) }
            } else if (any is ItemStack) repairItem(any)
        }

        @Suppress("DEPRECATION")
        private fun repairItem(item: ItemStack?) {
            if (item == null || item.type == Material.AIR || item.type.isBlock || item.type.isEdible || item.type.maxDurability <= 0 || item.durability == (0).toShort())
                return
            item.durability = (0).toShort()
        }
    }
}