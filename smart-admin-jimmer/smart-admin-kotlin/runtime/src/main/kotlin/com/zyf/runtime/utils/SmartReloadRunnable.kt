package com.zyf.runtime.utils

import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.runtime.config.AbstractSmartReloadCommand
import com.zyf.runtime.config.SmartReloadManager
import com.zyf.runtime.domain.SmartReloadItem
import com.zyf.runtime.domain.SmartReloadObject
import com.zyf.runtime.domain.SmartReloadResult
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap

/**
 * reload 线程
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Slf4j
class SmartReloadRunnable(private val reloadCommand: AbstractSmartReloadCommand) : Runnable {
    private var isInit = false

    override fun run() {
        try {
            this.doTask()
        } catch (e: Throwable) {
            log.error("", e)
        }
    }

    /**
     * 检测Identifier变化，执行reload
     */
    private fun doTask() {
        if (!isInit) {
            reloadCommand.init()
            isInit = true
            return
        }

        val smartReloadItemList = reloadCommand.readReloadItem()
        val tagIdentifierMap: ConcurrentHashMap<String, String> = reloadCommand.tagIdentifierMap
        for (smartReloadItem in smartReloadItemList!!) {
            val tag = smartReloadItem.tag
            val tagIdentifier = smartReloadItem.identification
            val preTagChangeIdentifier = tagIdentifierMap[tag]
            // 数据不一致
            if (preTagChangeIdentifier == null || preTagChangeIdentifier != tagIdentifier) {
                reloadCommand.putIdentifierMap(tag!!, tagIdentifier!!)
                // 执行重新加载此项的动作
                val smartReloadResult = this.doReload(smartReloadItem)
                reloadCommand.handleReloadResult(smartReloadResult)
            }
        }
    }

    /**
     * 方法调用
     *
     * @param smartReloadItem
     * @return
     */
    private fun doReload(smartReloadItem: SmartReloadItem): SmartReloadResult {
        val result = SmartReloadResult()
        val smartReloadObject: SmartReloadObject? = SmartReloadManager.getReloadObject(smartReloadItem.tag!!)
        try {
            if (smartReloadObject == null) {
                result.result = false
                result.exception = "不能从系统中找到对应的tag：" + smartReloadItem.tag
                return result
            }

            val method = smartReloadObject.method
            if (method == null) {
                result.result = false
                result.exception = "reload方法不存在"
                return result
            }

            result.tag = smartReloadItem.tag
            result.args = smartReloadItem.args
            result.identification = smartReloadItem.identification
            result.result = true
            val paramCount = method.parameterCount
            if (paramCount > 1) {
                result.result = false
                result.exception = "reload方法" + method.name + "参数太多"
                return result
            }

            if (paramCount == 0) {
                method.invoke(smartReloadObject.reloadObject)
            } else {
                method.invoke(smartReloadObject.reloadObject, smartReloadItem.args)
            }
        } catch (throwable: Throwable) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)

            result.result = false
            result.exception = throwable.toString()
        }
        return result
    }
}
