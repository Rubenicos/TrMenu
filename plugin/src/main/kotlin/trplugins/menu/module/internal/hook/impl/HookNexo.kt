package trplugins.menu.module.internal.hook.impl

import com.nexomc.nexo.api.NexoItems
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract

class HookNexo : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }

    fun getItem(id: String): ItemStack {
        if (checkHooked()) {
            return NexoItems.itemFromId(id)?.build() ?: empty
        }
        return empty
    }

    fun getId(itemStack: ItemStack): String? {
        if (checkHooked()) {
            return NexoItems.idFromItem(itemStack)
        }
        return "UNHOOKED"
    }
}