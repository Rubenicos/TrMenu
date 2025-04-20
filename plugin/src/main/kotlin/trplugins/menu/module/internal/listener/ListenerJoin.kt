package trplugins.menu.module.internal.listener

import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import trplugins.menu.TrMenu
import trplugins.menu.module.internal.data.Metadata
import trplugins.menu.util.bukkit.Heads

/**
 * @author Arasple
 * @date 2021/1/27 12:14
 */
object ListenerJoin {

    @SubscribeEvent
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        submit(async = true, delay = TrMenu.SETTINGS.getLong("Database.Join_Load_Delay", 20)) {
            // 缓存玩家头颅备用
            Heads.getHead(player.name)
            // 加载 Metadata - Data 数据
            Metadata.loadData(player)
        }
    }

}