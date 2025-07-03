package trplugins.menu.api.action.impl.send

import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.submit
import taboolib.platform.Folia
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents
import trplugins.menu.api.utils.FoliaUtil

/**
 * TrMenu
 * trplugins.menu.api.action.impl.chat.Command
 *
 * @author Score2
 * @since 2022/02/14 9:03
 */
class Command(handle: ActionHandle) : ActionBase(handle) {

    override val regex = "command|cmd|player|execute".toRegex()

    override fun onExecute(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        placeholderPlayer.parseContentSplited(contents.stringContent(), ";").forEach {
            // Folia 必须使用实体调度器 暂时不做优雅的兼容性处理 后期其他功能需要单独处理Folia时再修改
            if (Folia.isFolia) {
                FoliaUtil.invokeCommandWithEntityScheduler(player, it)
            }else{
                submit(async = false) {
                    player.performCommand(it)
                }
            }
        }
    }

}