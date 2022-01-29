package trplugins.menu.api.action.impl

import trplugins.menu.api.action.base.AbstractAction
import trplugins.menu.api.action.base.ActionOption
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer
import trplugins.menu.api.action.base.ActionDesc

/**
 * @author Arasple
 * @date 2021/1/31 11:36
 */
class ActionActionbar(content: String, option: ActionOption) : AbstractAction(content, option) {

    override fun onExecute(player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        player.sendActionBar(parseContent(placeholderPlayer))
    }

    companion object : ActionDesc {

        override val name = "action(bar)?s?".toRegex()

        override val parser: (Any, ActionOption) -> AbstractAction = { value, option ->
            ActionActionbar(value.toString(), option)
        }

    }

}