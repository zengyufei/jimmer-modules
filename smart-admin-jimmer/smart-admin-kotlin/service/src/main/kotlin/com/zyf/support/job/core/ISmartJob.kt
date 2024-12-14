package com.zyf.support.job.core

/**
 * 定时任务 执行接口
 *
 * @author huke
 * @date 2024/6/17 21:30
 */
interface ISmartJob {
    val className: String
        /**
         * 默认方法
         * 获取当前任务类名
         *
         * @return
         */
        get() = this.javaClass.name

    /**
     * 执行定时任务
     *
     * @param param 可选参数 任务不需要时不用管
     * @return 可null, 自行组织语言描述执行结果，例如：本次处理数据N条 等
     */
    fun run(param: String?): String?
}
