package trplugins.menu.api.utils

import io.papermc.paper.threadedregions.scheduler.EntityScheduler
import taboolib.common.platform.ProxyPlayer
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.platform.BukkitPlugin

/**
 * @author 大阔
 * @since 2025/1/25 00:16
 */
object FoliaUtil {

    fun invokeCommandWithEntityScheduler(player: ProxyPlayer,command:String){
        getEntityScheduler(player).execute(BukkitPlugin.getInstance(),{
            player.performCommand(command)
        },null,0)
    }

    fun getEntityScheduler(player: ProxyPlayer): EntityScheduler {
        return player.cast<Any>().invokeMethod<EntityScheduler>("getScheduler")!!
    }

}