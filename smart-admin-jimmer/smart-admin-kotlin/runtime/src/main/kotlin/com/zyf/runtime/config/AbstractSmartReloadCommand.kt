package com.zyf.runtime.config

import com.zyf.runtime.domain.SmartReloadItem
import com.zyf.runtime.domain.SmartReloadResult
import java.util.concurrent.ConcurrentHashMap


/**
 * 检测是否 Reload 的类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
abstract class AbstractSmartReloadCommand {
    /**
     * 当前ReloadItem的存储器
     */
    val tagIdentifierMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    fun init() {
        val smartReloadItems = this.readReloadItem()
        if (smartReloadItems != null) {
            for (smartReloadItem in smartReloadItems) {
                tagIdentifierMap[smartReloadItem.tag!!] = smartReloadItem.identification!!
            }
        }
    }


    /**
     * 该方法返回一个List<ReloadItem></ReloadItem>>:<br></br>
     * ReloadItem对象的tagIdentify为：该tag的 状态（状态其实就是个字符串，如果该字符串跟上次有变化则进行reload操作）<br></br>
     * ReloadItem对象的args为： reload操作需要的参数<br></br><br></br>
     *
     * @return List<ReloadItem>
    </ReloadItem> */
    abstract fun readReloadItem(): List<SmartReloadItem>?

    /**
     * 处理Reload结果
     *
     * @param smartReloadResult
     */
    abstract fun handleReloadResult(smartReloadResult: SmartReloadResult?)


    /**
     * 设置新的缓存标识
     *
     * @param tag
     * @param identification
     */
    fun putIdentifierMap(tag: String, identification: String) {
        tagIdentifierMap[tag] = identification
    }

}
