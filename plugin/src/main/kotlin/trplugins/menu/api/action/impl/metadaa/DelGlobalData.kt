package trplugins.menu.api.action.impl.metadaa

import taboolib.common.platform.ProxyPlayer
import trplugins.menu.api.action.ActionHandle
import trplugins.menu.api.action.base.ActionBase
import trplugins.menu.api.action.base.ActionContents
import trplugins.menu.module.internal.data.Metadata

/**
 * TrMenu
 * trplugins.menu.api.action.impl.metadaa.DelGlobalData
 *
 * @author Score2
 * @since 2022/02/14 14:28
 */
class DelGlobalData(handle: ActionHandle) : ActionBase(handle) {

    override val regex = "(remove|rem|del)-?(global|g)-?datas?".toRegex()

    override fun onExecute(contents: ActionContents, player: ProxyPlayer, placeholderPlayer: ProxyPlayer) {
        contents.stringContent().parseContentSplited(placeholderPlayer, ";").forEach { it ->
            val regex = Regex(it)
            Metadata.getGlobalDataKeys().filter { it.matches(regex) }.forEach { Metadata.setGlobalData(it, null)}
        }
    }
}