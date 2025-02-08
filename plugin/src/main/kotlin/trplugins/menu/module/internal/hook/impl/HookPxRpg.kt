package trplugins.menu.module.internal.hook.impl

import com.pxpmc.pxrpg.api.MAPI
import com.pxpmc.pxrpg.api.Module
import com.pxpmc.pxrpg.api.modules.item.ItemModule
import com.pxpmc.pxrpg.api.util.ParameterResolver
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import trplugins.menu.module.internal.hook.HookAbstract


class HookPxRpg : HookAbstract() {

    private val empty = buildItem(XMaterial.BEDROCK) { name = "UNHOOKED_${super.name.uppercase()}" }
    private val manager by lazy {
        Module.getModule(ItemModule::class.java).itemManager
    }
    /*
    id;level=2;bind=已绑定
     */


    fun getItem(id: String, player: Player? = null): ItemStack {
        if (checkHooked()) {
            val args: String? = id.split(",").getOrNull(1)
            val itemId: String = id.split(",")[0]
            val itemConfig = manager.getRegister(itemId)
            val pxPlayer = MAPI.getBukkitPxRpgAPI().toPxRpgPlayer(player)
            val argsParser = ParameterResolver()
            args?.let {
                argsParser.parser(args)
                val item = manager.spawnItemStack(
                    itemConfig,
                    pxPlayer,
                    null,
                    argsParser
                )
                return MAPI.getBukkitPxRpgAPI().toBukkitItemStack(item) ?: empty
            }

        }
        return empty
    }

    fun getId(itemStack: ItemStack): String {
        if (checkHooked()) {
            val item = manager.toThat(MAPI.getBukkitPxRpgAPI().toPxRpgItemStack(itemStack))
            return item.id;
        }
        return "UNHOOKED"
    }
}