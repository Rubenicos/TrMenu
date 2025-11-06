package trplugins.menu.module.display.item

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.platform.util.ItemBuilder
import trplugins.menu.module.display.MenuSession
import trplugins.menu.module.internal.script.evalScript
import trplugins.menu.util.Regexs

/**
 * @author Arasple
 * @date 2021/1/24 18:50
 * 显示物品的非动画, 支持动态的属性
 */
class Meta(
    val amount: String,
    val shiny: String,
    val flags: Array<ItemFlag>,
    val nbt: ItemTag?,
    val tooltip: String?,
    val itemModel: String?,
    val hideTooltip: String,
    val unbreakable: String,
    val data: String,
) {

    private val isAmountDynamic = amount.toIntOrNull() == null
    private val isShinyDynamic = !shiny.matches(Regexs.BOOLEAN)
    private val isHideTooltipDynamic = !hideTooltip.matches(Regexs.BOOLEAN)
    private val isNBTDynamic = nbt != null && Regexs.containsPlaceholder(nbt.toJsonSimplified())
    private val isUnbreakableDynamic = !unbreakable.matches(Regexs.BOOLEAN)
    private val isDataDynamic = data.toIntOrNull() == null
    val isDynamic = isAmountDynamic || isNBTDynamic || isShinyDynamic || isHideTooltipDynamic

    fun amount(session: MenuSession): Int {
        return (if (isAmountDynamic) session.parse(amount) else amount).toDoubleOrNull()?.toInt() ?: 1
    }

    fun shiny(session: MenuSession, builder: ItemBuilder) {
        if ((shiny.toBoolean()) || (isShinyDynamic && session.placeholderPlayer.evalScript(shiny).asBoolean())) {
            builder.shiny()
        }
        return
    }

    fun flags(builder: ItemBuilder) {
        if (flags.isNotEmpty()) {
            builder.flags.addAll(flags)
        }
    }

    fun nbt(session: MenuSession, itemStack: ItemStack): ItemMeta? {
        if (!nbt.isNullOrEmpty()) {
            val nbt = if (isNBTDynamic) ItemTag.fromJson(session.parse(nbt.toJson())) else nbt
            val tag = ItemTag()
            tag.putAll(itemStack.getItemTag())
            tag.putAll(nbt)
            tag.saveTo(itemStack)
            return itemStack.itemMeta
        }
        return null
    }

    fun hasAmount(): Boolean {
        return amount.isNotEmpty() || amount.toIntOrNull() != null
    }

    fun tooltipStyle(session: MenuSession, builder: ItemBuilder) {
        if (tooltip.isNullOrEmpty()) {
            return
        }
        val key = session.placeholderPlayer.evalScript(tooltip).asString().let { NamespacedKey.fromString(it) }
        builder.tooltipStyle = key
    }

    fun itemModel(session: MenuSession, builder: ItemBuilder) {
        if (itemModel.isNullOrEmpty()) {
            return
        }
        val key = session.placeholderPlayer.evalScript(itemModel).asString().let { NamespacedKey.fromString(it) }
        builder.itemModel = key
    }

    fun hideTooltip(session: MenuSession, builder: ItemBuilder) {
        if (hideTooltip.toBoolean() || (isHideTooltipDynamic && session.placeholderPlayer.evalScript(hideTooltip).asBoolean())) {
            builder.isHideTooltip = true
        }
    }

    fun unbreakable(session: MenuSession, builder: ItemBuilder) {
        if (unbreakable.toBoolean() || (isUnbreakableDynamic && session.placeholderPlayer.evalScript(unbreakable).asBoolean())) {
            builder.isUnbreakable = true
        }
    }

    fun data(session: MenuSession, builder: ItemBuilder) {
        if (data.isEmpty()) {
            return
        }
        val evalData = session.parse(amount).toIntOrNull() ?: session.placeholderPlayer.evalScript(data).asInt(0)
        builder.damage = evalData
    }

}