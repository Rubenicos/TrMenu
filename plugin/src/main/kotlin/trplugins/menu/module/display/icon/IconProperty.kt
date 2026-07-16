package trplugins.menu.module.display.icon

import taboolib.common.platform.function.adaptPlayer
import trplugins.menu.api.reaction.Reactions
import trplugins.menu.api.receptacle.ReceptacleClickType
import trplugins.menu.module.display.MenuSession
import trplugins.menu.module.display.item.Item
import trplugins.menu.util.Regexs

/**
 * @author Arasple
 * @date 2021/1/25 10:53
 */
class IconProperty(
    val priority: Int,
    val condition: String,
    val display: Item,
    val action: Map<Set<ReceptacleClickType>, Reactions>
) {

    fun isTextureUpdatable(): Boolean {
        return display.meta.isDynamic || display.texture.cyclable() || display.texture.elements.any { it.dynamic }
    }

    fun isNameUpdatable(session: MenuSession): Boolean {
        return display.name(session).cyclable() || display.name(session).elements.any { Regexs.containsPlaceholder(it) }
    }

    fun isLoreUpdatable(session: MenuSession): Boolean {
        return display.lore(session).cyclable() || display.lore(session).elements.any { lore ->
            lore.lore.any { (line, condition) -> condition != null || Regexs.containsPlaceholder(line) }
        }
    }

    fun handleClick(type: ReceptacleClickType, session: MenuSession) {
        val reactions = action.entries
            .filter { entry ->
                entry.key.contains(type) || entry.key.any {
                    it == ReceptacleClickType.ALL ||
                    it == ReceptacleClickType.NUMBER_KEY && type.isNumberKeyClick()
                }
            }
            .map { it.value }

        reactions.forEach { it.eval(adaptPlayer(session.viewer)) }
    }


}
