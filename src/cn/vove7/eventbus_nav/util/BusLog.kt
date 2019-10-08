package cn.vove7.eventbus_nav.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * # BusLog
 * Created by 11324.
 * Date: 2019/9/19
 */
object BusLog : Logger by LoggerFactory.getLogger("-") {

    var out = File(".idea").exists()

    private fun getCallerInfo(): String {
        val s = Thread.currentThread().stackTrace[3]
        return "${s.className.let { it.substring(it.lastIndexOf('.') + 1) }}.${s.methodName}(${s.fileName}:${s.lineNumber})"
    }

    @JvmStatic
    fun d(p: Any?) {
        if (out.not()) return
        debug(getCallerInfo() + "  " + p.toString())
    }

    @JvmStatic
    fun i(p: Any?) {
        if (out.not()) return
        info(getCallerInfo() + "  " + p.toString())
    }

    @JvmStatic
    fun e(p: Any?) {
        if (out.not()) return
        error(getCallerInfo() + "  " + p.toString())
    }

//    fun ss(): String {
//        val st = Thread.currentThread().stackTrace
//        return arrayOf(st[3], st[4], st[5]).joinToString(prefix = "\n-----------------\n", postfix = "\n-----------------\n",
//                separator = " > ") { s ->
//            "${s.className.let { it.substring(it.lastIndexOf('.') + 1) }}.${s.methodName}(${s.fileName}:${s.lineNumber})"
//        }
//    }
}