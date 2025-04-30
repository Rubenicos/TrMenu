package trplugins.menu.module.internal.database

import taboolib.expansion.db
import taboolib.expansion.persistentContainer
import trplugins.menu.TrMenu
import trplugins.menu.module.internal.data.GlobalDataEntity

class GlobalDataDao {

    private val table = "${TrMenu.SETTINGS.getString("Database.SQL.prefix", "trmenu")}_global_data".lowercase()

    private val container by lazy {
        persistentContainer(type = db("settings.yml", "Database.SQL", "global.db")) { new<GlobalDataEntity>(table) }
    }

    fun update(key: String, value: Any?) {
        val containerx = container[table]
        val old = get(key)
        if (old == null) {
            containerx.insert(listOf(GlobalDataEntity(key, value.toString())))
        } else {
            val tablex = containerx.table
            val dataSource = containerx.dataSource
            tablex.update(dataSource) {
                where("key" eq key)
                set("data", value?.toString() ?: "")
            }

        }
    }

    fun get(key: String): GlobalDataEntity? {
        return container[table].getOne<GlobalDataEntity?> {
            "key" eq key
        }
    }

    fun get(): List<GlobalDataEntity> {
        return container[table].get<GlobalDataEntity?>().filter { it?.data?.isNotEmpty() == true }.mapNotNull { it }
    }

    companion object {
        val door by lazy { GlobalDataDao() }
    }
}