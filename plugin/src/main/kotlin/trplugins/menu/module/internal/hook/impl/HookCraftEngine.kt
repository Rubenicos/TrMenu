package trplugins.menu.module.internal.hook.impl

import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.plugin.BukkitCraftEngine
import net.momirealms.craftengine.core.util.Key
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract

class HookCraftEngine : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }

    fun getItem(material: String, player: Player): ItemStack {
        val (namespace, id) = material.split(":", limit = 2)
        val craftPlayer = BukkitCraftEngine.instance().adapt(player)
        val item = CraftEngineItems.byId(Key(namespace, id)) ?: return empty
        return item.buildItemStack(craftPlayer)
    }
}