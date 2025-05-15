package trplugins.menu.module.internal.data

import org.bukkit.entity.Player
import taboolib.expansion.Length
import java.util.*


data class DataEntity(
    val user: UUID,
    val key: String,
    @Length(-1)
    val data: String
) {

    companion object {
        fun constructor(player: Player, key: String, data: String): DataEntity {
            return DataEntity(player.uniqueId, key, data)
        }
    }
}
