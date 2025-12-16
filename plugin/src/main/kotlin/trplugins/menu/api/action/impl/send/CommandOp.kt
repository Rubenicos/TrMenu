package trplugins.menu.api.action.impl.send

import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.submit
import taboolib.expansion.dispatchCommandAsOp
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents
import trplugins.menu.module.display.session

/**
 * TrMenu
 * trplugins.menu.api.action.impl.send.CommandOp
 *
 * @author Score2
 * @since 2022/02/14 12:29
 */
class CommandOp(handle: ActionHandle) : ActionBase(handle) {

    override val regex = "op(erator)?s?".toRegex()

    override fun onExecute(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        val fakeOp = player.session().menu?.settings?.commandFakeOp ?: true
        contents.stringContent().parseContentSplited(placeholderPlayer, ";").forEach {
            submit(async = false) {
                if (fakeOp) {
                    player.cast<Player>().dispatchCommandAsOp(it)
                } else {
                    player.isOp.let { isOp ->
                        player.isOp = true
                        try {
                            player.performCommand(it)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        } finally {
                            player.isOp = isOp
                        }
                    }
                }
            }
        }
    }

}