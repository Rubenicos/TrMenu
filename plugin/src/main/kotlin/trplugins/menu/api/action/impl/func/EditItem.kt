package trplugins.menu.api.action.impl.func

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.inventory.meta.PotionMeta
import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.replaceWithOrder
import taboolib.library.xseries.XEnchantment
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.MinecraftVersion
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents
import trplugins.menu.module.display.session
import trplugins.menu.util.Regexs
import trplugins.menu.util.bukkit.ItemHelper
import kotlin.jvm.optionals.getOrNull

/**
 * TrMenu
 * trplugins.menu.api.action.impl.func.EditItem
 *
 * @author Rubenicos
 * @since 2022/02/14 13:15
 */
class EditItem(handle: ActionHandle) : ActionBase(handle) {

    override val regex = "edit(-)?items?".toRegex()

    @Suppress("UNCHECKED_CAST")
    override fun onExecute(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        val method: Int by contents
        val itemString = contents["item"] as String // 名称重合
        val part: Pair<Int, String> by contents
        val list = contents["value"] as List<String> // 伞兵 idea

        if (method < 1 || itemString.isBlank() || part.first < 1 || list.isEmpty()) return
        val item = ItemHelper.fromPlayerInv(player.cast<Player>().inventory, itemString) ?: return
        val value = placeholderPlayer.session().parse(list)

        fun material(item: ItemStack, material: Material) {
            item.type = material
        }

        fun name(item: ItemStack, name: String?) {
            val meta = item.itemMeta?: return
            if (name == null) {
                meta.setDisplayName(null)
            } else {
                meta.setDisplayName((if (method == 3 && meta.hasDisplayName()) meta.displayName else "") + name)
            }
            item.itemMeta = meta
        }

        fun lore(item: ItemStack, value: List<String>) {
            val meta = item.itemMeta?: return
            var lore = meta.lore?: arrayListOf<String>()
            if (part.second.isBlank()) {
                when (method) {
                    1 -> lore = value
                    2 -> lore.clear()
                    3 -> lore.addAll(value)
                }
            } else {
                val index = if (part.second == "last") lore.size - 1 else part.second.toIntOrNull()?: 0
                if (lore.size >= index) {
                    when (method) {
                        1 -> lore[index] = value[0]
                        2 -> lore.removeAt(index)
                        3 -> lore.addAll(index, value)
                    }
                } else return
            }
            meta.lore = lore
            item.itemMeta = meta
        }

        fun flags(item: ItemStack, flags: List<ItemFlag>) {
            val meta = item.itemMeta?: return
            when (method) {
                1 -> {
                    meta.itemFlags.forEach { meta.removeItemFlags(it) }
                    flags.forEach { meta.addItemFlags(it) }
                }
                2 -> flags.forEach { if (meta.hasItemFlag(it)) meta.removeItemFlags(it) }
                3 -> flags.forEach { if (!meta.hasItemFlag(it)) meta.addItemFlags(it) }
            }
            item.itemMeta = meta
        }

        fun customModelData(item: ItemStack, model: Int?) {
            val meta = item.itemMeta?: return
            meta.setCustomModelData(model)
            item.itemMeta = meta
        }

        fun enchantment(item: ItemStack, enchant: Enchantment, level: Int) {
            when (method) {
                1 -> {
                    Enchantment.values().forEach { item.removeEnchantment(it) }
                    item.addUnsafeEnchantment(enchant, level)
                }
                2 -> item.removeEnchantment(enchant)
                3 -> item.addUnsafeEnchantment(enchant, level)
            }
        }

        fun color(item: ItemStack, color: Color?) {
            val meta = item.itemMeta ?: return
            if (method == 3) {
                when {
                    meta is LeatherArmorMeta -> meta.setColor(meta.color.mixColors(color))
                    MinecraftVersion.majorLegacy < 11100 -> return
                    meta is PotionMeta -> {
                        if (meta.color == null) {
                            meta.color = color
                        } else {
                            meta.color = meta.color!!.mixColors(color)
                        }
                    }
                    meta is MapMeta -> {
                        if (meta.color == null) {
                            meta.color = color
                        } else {
                            meta.color = meta.color!!.mixColors(color)
                        }
                    }
                }
            } else {
                when {
                    meta is LeatherArmorMeta -> meta.setColor(color)
                    MinecraftVersion.majorLegacy < 11100 -> return
                    meta is PotionMeta -> meta.color = color
                    meta is MapMeta -> meta.color = color
                }
            }
            item.itemMeta = meta
        }

        when (part.first) {
            1 -> { // Material
                if (method == 1) {
                    val material = XMaterial.matchXMaterial(value[0])
                    if (material.isPresent) {
                        val mat = material.get().get() ?: return
                        if (item is Array<*>) {
                            item.forEach { (it as ItemStack?)?.let { item -> material(item, mat) } }
                        } else if (item is ItemStack) material(item, mat)
                    }
                }
            }
            2 -> { // Name
                val name = if (method == 2) null else value.joinToString(" ")
                if (item is Array<*>) {
                    item.forEach { (it as ItemStack?)?.let { item -> name(item, name) } }
                } else if (item is ItemStack) name(item, name)
            }
            3 -> { // Lore
                if (item is Array<*>) {
                    item.forEach { (it as ItemStack?)?.let { item -> lore(item, value) } }
                } else if (item is ItemStack) lore(item, value)
            }
            4 -> { // Flags
                val flags: MutableList<ItemFlag> = ArrayList()
                value.forEach {
                    try {
                        flags.add(ItemFlag.valueOf(it))
                    } catch (ignored: Exception) { }
                }
                if (flags.isNotEmpty()) {
                    if (item is Array<*>) {
                        item.forEach { (it as ItemStack?)?.let { item -> flags(item, flags) } }
                    } else if (item is ItemStack) flags(item, flags)
                }
            }
            5 -> { // CustomModelData
                val model = when (method) {
                    1 -> value[0].toIntOrNull() ?: return
                    2 -> null
                    else -> return
                }
                if (item is Array<*>) {
                    item.forEach { (it as ItemStack?)?.let { item -> customModelData(item, model) } }
                } else if (item is ItemStack) customModelData(item, model)
            }
            6 -> { // Enchantment
                val enchant = XEnchantment.of(value[0]).getOrNull()?.get() ?: return
                val level = if (value.size == 1) 1 else value[1].toIntOrNull() ?: return
                if (item is Array<*>) {
                    item.forEach { (it as ItemStack?)?.let { item -> enchantment(item, enchant, level) } }
                } else if (item is ItemStack) enchantment(item, enchant, level)
            }
            7 -> { // Color
                val color = if (method == 2 || value.isEmpty()) null else {
                    val values = if (value[0].contains(',')) value[0].split(',') else value
                    if (values.size < 3) return
                    Color.fromRGB(
                        values[0].toIntOrNull() ?: return,
                        values[1].toIntOrNull() ?: return,
                        values[2].toIntOrNull() ?: return
                    )
                }
                if (item is Array<*>) {
                    item.forEach { (it as ItemStack?)?.let { item -> color(item, color) } }
                } else if (item is ItemStack) color(item, color)
            }
        }
    }

    override fun readContents(contents: Any): ActionContents {
        val actionContents = ActionContents()
        var method: Int by actionContents
        var item: String by actionContents
        var part: Pair<Int, String> by actionContents
        var value: List<String> by actionContents

        val content: String = contents.toString()
        val split = content.split(" ", limit = 4)
        var rawValue = split.getOrElse(3) { "" }

        val replacements = Regexs.SENTENCE.findAll(rawValue).mapIndexed { index, result ->
            rawValue = rawValue.replace(result.value, "{$index}")
            index to result.groupValues[1].replace("\\s", " ")
        }.toMap().values.toTypedArray()

        val replacedValue: MutableList<String> = ArrayList()
        rawValue.split(" ").toTypedArray().forEach { s -> replacedValue.add(s.replaceWithOrder(*replacements)) }

        method = when (split.getOrElse(0) { "" }.lowercase()) {
            "set", "put" -> 1
            "delete", "remove" -> 2
            "add", "concat" -> 3
            else -> 0
        }
        item = split.getOrElse(1) { "" }
        part = split.getOrElse(2) { "" }.lowercase().split(":", ";", "=", limit = 2).let { part ->
            when (part.getOrElse(0) { "" }) {
                "mat", "material", "type" -> 1
                "name", "displayname" -> 2
                "lore", "description" -> 3
                "flag", "flags" -> 4
                "custommodeldata", "modeldata", "model" -> 5
                "enchantment", "enchant" -> 6
                "color" -> 7
                else -> 0
            } to part.getOrElse(1) { "" }
        }
        value = replacedValue

        return actionContents
    }

}