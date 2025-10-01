package trplugins.menu.module.internal.listener

import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Baffle
import taboolib.module.nms.MinecraftVersion.versionId
import taboolib.module.nms.nmsProxy
import taboolib.module.nms.obcClass
import trplugins.menu.TrMenu
import trplugins.menu.api.event.MenuOpenEvent
import trplugins.menu.module.display.Menu
import trplugins.menu.module.display.MenuSession
import trplugins.menu.util.reloadable
import java.util.concurrent.TimeUnit

/**
 * @author Arasple
 * @date 2021/1/29 17:18
 */
object ListenerItemInteract {

    internal val interactCooldown = reloadable {
        Baffle.of(TrMenu.SETTINGS.getLong("Menu.Settings.Bound-Item-Interval", 2000), TimeUnit.MILLISECONDS)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInteract(e: PlayerInteractEvent) {
        ListenerItemInteract::interactCooldown.get()

        if (versionId >= 10900 && e.hand == EquipmentSlot.OFF_HAND) return
        val player = e.player
        val item = e.item ?: return
        val session = MenuSession.getSession(player)

        if (!InventoryNMS.instance.checkInventory(player.openInventory.topInventory)
            || !InventoryNMS.instance.checkInventory(player.inventory)
        ) return

        if (player.openInventory.topInventory.holder != (player.inventory as Inventory).holder || session.menu != null) return
        if (interactCooldown.value.hasNext(player.name)) {
            val menu = Menu.menus.find { it -> it.settings.boundItems.any { it.itemMatches(item, true) } }
            if (menu != null) {
                e.isCancelled = true
                menu.open(player, reason = MenuOpenEvent.Reason.BINDING_ITEMS)
            }
        }
    }

    class InventoryNMSImpl : InventoryNMS() {

        private val `clazz$CraftInventoryCustom$MinecraftInventory` =
            obcClass("inventory.CraftInventoryCustom\$MinecraftInventory")

        override fun checkInventory(inventory: Inventory): Boolean {
            return `clazz$CraftInventoryCustom$MinecraftInventory`.isInstance((inventory as CraftInventory).inventory)
        }

    }

    abstract class InventoryNMS {
        abstract fun checkInventory(inventory: Inventory): Boolean

        companion object {
            val instance by lazy { nmsProxy<InventoryNMS>() }
        }
    }

}