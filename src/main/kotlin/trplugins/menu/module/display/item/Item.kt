package trplugins.menu.module.display.item

import trplugins.menu.api.menu.IItem
import trplugins.menu.module.display.MenuSession
import trplugins.menu.module.display.texture.Texture
import trplugins.menu.util.bukkit.ItemHelper.defColorize
import trplugins.menu.util.collections.CycleList
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

/**
 * @author Arasple
 * @date 2021/1/25 10:48
 */
class Item(
    val texture: CycleList<Texture>,
    val name: CycleList<String>,
    val lore: CycleList<Lore>,
    val meta: Meta
) : IItem {

    internal val cache = mutableMapOf<Int, ItemStack>()

    private fun name(session: MenuSession) = this.name.current(session.id)?.let { defColorize(session.parse(it)) }

    private fun lore(session: MenuSession) = this.lore.current(session.id)?.parse(session)?.map { defColorize(it, true) }

    fun get(session: MenuSession): ItemStack {
        return if (cache.containsKey(session.id)) cache[session.id]!!
        else build(session)
    }

    private fun build(
        session: MenuSession,
        name: String? = name(session),
        lore: List<String>? = lore(session)
    ): ItemStack {
        val item = generate(session, texture.current(session.id)!!, name, lore, meta)
        cache[session.id] = item
        return item
    }

    override fun updateTexture(session: MenuSession) {
        texture.cycleIndex(session.id)

        if (!cache.containsKey(session.id))
            build(session)
        else if (cache[session.id]!!.itemMeta == null)
            build(session)
        else
            cache[session.id]!!.itemMeta?.let {
                build(session, it.displayName, it.lore ?: listOf())
            }
    }

    override fun updateName(session: MenuSession) {
        name.cycleIndex(session.id)

        if (!cache.containsKey(session.id))
            build(session)
        else {
            val current = cache[session.id]
            try {
                val new = buildItem(current!!) { name = name(session) }
                cache[session.id] = new
            } catch (t: Throwable) {

            }
        }
    }

    override fun updateLore(session: MenuSession) {
        lore.cycleIndex(session.id)

        if (!cache.containsKey(session.id)) {
            build(session)
        } else {
            val current = cache[session.id]
            if (current != null && current.type != Material.AIR) {
                val new = buildItem(current) {
                    lore.clear()
                    lore.addAll(lore(session) ?: listOf())
                }
                cache[session.id] = new
            }
        }
    }

}