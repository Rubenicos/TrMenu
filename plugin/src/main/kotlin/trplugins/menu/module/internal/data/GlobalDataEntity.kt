package trplugins.menu.module.internal.data

import taboolib.expansion.Length

data class GlobalDataEntity(
    val key: String,
    @Length(-1)
    val data: String
)
