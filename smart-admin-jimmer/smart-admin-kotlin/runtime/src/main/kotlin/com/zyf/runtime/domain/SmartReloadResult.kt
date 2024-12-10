package com.zyf.runtime.domain

/**
 * t_reload_result 表 实体类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class SmartReloadResult {
    /**
     * 项名称
     */
    var tag: String? = null

    /**
     * 参数
     */
    var args: String? = null

    /**
     * 标识
     */
    var identification: String? = null

    /**
     * 处理结果
     */
    var result: Boolean = false

    /**
     * 异常说明
     */
    var exception: String? = null
}
