package trplugins.menu.api.action.impl

import trplugins.menu.api.TrMenuAPI
import trplugins.menu.api.action.base.AbstractAction
import trplugins.menu.api.action.base.ActionOption
import org.bukkit.entity.Player
import trplugins.menu.api.action.InternalAction
import trplugins.menu.api.action.base.ActionDesc

/**
 * @author Arasple
 * @date 2021/2/1 12:31
 */
class ActionKether(content: String, option: ActionOption) : InternalAction(content, option) {

    override fun onExecute(player: Player, placeholderPlayer: Player) {
        TrMenuAPI.eval(player, baseContent)
    }

    companion object : ActionDesc {

        override val name = "ke(ther)?s?".toRegex()

        override val parser: (Any, ActionOption) -> AbstractAction = { it, option ->
            ActionKether(it.toString(), option)
        }

    }

}
