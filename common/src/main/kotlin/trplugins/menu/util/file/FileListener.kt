package trplugins.menu.util.file

import taboolib.common.platform.function.console
import taboolib.common5.FileWatcher
import taboolib.module.lang.sendErrorMessage
import java.io.File
import java.util.function.Consumer


/**
 * @author Arasple
 * @date 2020/7/28 11:43
 */
object FileListener {

    private val listening = mutableSetOf<File>()

//    fun isListening(file: File): Boolean {
//        return watcher.hasListener(file)
//    }

    fun listener(file: File, runnable: File.() -> Unit) {
        watcher.addSimpleListener(file, runnable)
        listening.add(file)
    }

    fun clear() {
        var count = 0
        listening.removeIf {
            val remove = !it.exists()
            if (remove) {
                watcher.removeListener(it)
                count++
            }
            remove
        }
        if (count > 0) {
            console().sendMessage("DEBUG: CLEARED $count unused listeners")
//            println("DEBUG: CLEARED $count unused listeners")
        }
    }

//    @TFunction.Cancel
//    fun uninstall() {
//        watcher.unregisterAll()
//    }

    val watcher = FileWatcher.INSTANCE

}