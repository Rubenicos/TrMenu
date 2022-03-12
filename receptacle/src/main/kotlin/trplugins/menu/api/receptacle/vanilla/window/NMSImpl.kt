package trplugins.menu.api.receptacle.vanilla.window

import net.minecraft.server.v1_16_R3.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.common.reflect.Reflex.Companion.unsafeInstance
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket
import taboolib.platform.util.isAir
import trplugins.menu.api.receptacle.hook.HookFloodgate

/**
 * @author Arasple
 * @date 2020/12/4 21:25
 */
class NMSImpl : NMS() {

    private val emptyItemStack: net.minecraft.server.v1_16_R3.ItemStack? = CraftItemStack.asNMSCopy((ItemStack(Material.AIR)))

    val staticInventories = HashMap<Player, Pair<Int, Inventory>>()

    private val Player.staticContainerId get() = staticInventories[this]?.first
    private val Player.staticInventory get() = staticInventories[this]?.second
    private fun Player.isBedrockPlayer() = HookFloodgate.isBedrockPlayer(this)

    override fun sendWindowsClose(player: Player, windowId: Int) {
        if (player.isBedrockPlayer()) {
            player.closeInventory()
            staticInventories.remove(player)
        } else {
            player.sendPacket(PacketPlayOutCloseWindow(windowId))
        }
    }

    override fun sendWindowsItems(player: Player, windowId: Int, items: Array<ItemStack?>) {
        when {
            player.isBedrockPlayer() -> {
                items.forEach { player.staticInventory?.addItem(it) }
            }
            MinecraftVersion.majorLegacy >= 11701 -> {
                sendPacket(
                    player,
                    PacketPlayOutWindowItems::class.java.unsafeInstance(),
                    "containerId" to windowId,
                    "stateId" to 1,
                    "items" to items.map { i -> toNMSCopy(i) }.toList(),
                    "carriedItem" to emptyItemStack
                )
            }
            MinecraftVersion.majorLegacy >= 11700 -> {
                sendPacket(
                    player,
                    PacketPlayOutWindowItems::class.java.unsafeInstance(),
                    "containerId" to windowId,
                    "items" to items.map { i -> toNMSCopy(i) }.toList()
                )
            }
            MinecraftVersion.majorLegacy >= 11000 -> {
                sendPacket(
                    player,
                    PacketPlayOutWindowItems::class.java.unsafeInstance(),
                    "a" to windowId,
                    "b" to items.map { i -> toNMSCopy(i) }.toList()
                )
            }
            else -> {
                sendPacket(
                    player,
                    PacketPlayOutWindowItems::class.java.unsafeInstance(),
                    "a" to windowId,
                    "b" to items.map { i -> toNMSCopy(i) }.toTypedArray()
                )
            }
        }
    }

    override fun sendWindowsOpen(player: Player, windowId: Int, type: WindowLayout, title: String) {
        when {
            player.isBedrockPlayer() -> {
                val inventory = Bukkit.createInventory(null, type.toBukkitType(), title)
                player.openInventory(inventory)
                val windowId = player.getProperty<Int>("entity/containerCounter")!!
                staticInventories[player] = Pair(windowId, inventory)
            }
            MinecraftVersion.isUniversal -> {
                sendPacket(
                    player,
                    PacketPlayOutOpenWindow::class.java.unsafeInstance(),
                    "containerId" to windowId,
                    "type" to type.vanillaId,
                    "title" to CraftChatMessage.fromStringOrNull(title)
                )
            }
            MinecraftVersion.majorLegacy >= 11400 -> {
                sendPacket(
                    player,
                    PacketPlayOutOpenWindow(),
                    "a" to windowId,
                    "b" to type.vanillaId,
                    "c" to CraftChatMessage.fromStringOrNull(title)
                )
            }
            else -> {
                sendPacket(
                    player,
                    PacketPlayOutOpenWindow(),
                    "a" to windowId,
                    "b" to type.id,
                    "c" to ChatComponentText(title),
                    "d" to type.containerSize - 1 // Fixed ViaVersion can not view 6x9 menu bug.
                )
            }
        }
    }

    override fun sendWindowsSetSlot(player: Player, windowId: Int, slot: Int, itemStack: ItemStack?, stateId: Int) {
        when {
            player.isBedrockPlayer() -> {
                player.staticInventory!!.setItem(slot, itemStack)
            }
            MinecraftVersion.majorLegacy >= 11701 -> {
                sendPacket(
                    player,
                    PacketPlayOutSetSlot::class.java.unsafeInstance(),
                    "containerId" to windowId,
                    "stateId" to -1,
                    "slot" to slot,
                    "itemStack" to toNMSCopy(itemStack)
                )
            }
            else -> {
                player.sendPacket(PacketPlayOutSetSlot(windowId, slot, toNMSCopy(itemStack)))
            }
        }
    }

    override fun sendWindowsUpdateData(player: Player, windowId: Int, property: Int, value: Int) {
        TODO("Not yet implemented")
    }

    fun toNMSCopy(itemStack: ItemStack?): net.minecraft.server.v1_16_R3.ItemStack? {
        return if (itemStack.isAir()) emptyItemStack else CraftItemStack.asNMSCopy(itemStack)
    }

    fun sendPacket(player: Player, packet: Any, vararg fields: Pair<String, Any?>) {
        fields.forEach { packet.setProperty(it.first, it.second) }
        player.sendPacket(packet)
    }
}