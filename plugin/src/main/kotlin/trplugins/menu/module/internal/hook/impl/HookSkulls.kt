package trplugins.menu.module.internal.hook.impl

import ca.tweetzy.skulls.Skulls
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract
import trplugins.menu.util.bukkit.Heads

/**
 * TrMenu
 * trmenu.HookSkulls
 *
 * @author Score2
 * @since 2022/01/06 20:57
 */
class HookSkulls : HookAbstract() {

    private val empty = buildItem(XMaterial.PLAYER_HEAD) { name = "UNKNOWN_${super.name.uppercase()}" }

    fun getSkull(id: String): ItemStack {
        return Skulls.getAPI().getSkullItem(id.toIntOrNull() ?: 1) ?: empty
    }

    fun getRandomSkull(): ItemStack {
        return Skulls.getAPI().randomSkull?.itemStack ?: empty
    }

    fun getId(itemStack: ItemStack): String? {
        return Skulls.getSkullManager().skulls.find { it.texture == Heads.seekTexture(itemStack) }?.texture
    }

}