package com.zyf.support.repeatsubmit.annoation

/**
 * 标记 需要防止重复提交 的注解<br></br>
 * 单位：毫秒
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2020-11-25 20:56:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class RepeatSubmit(
    /**
     * 重复提交间隔时间/毫秒
     *
     * @return
     */
    val value: Int = 300
) {
    companion object {
        /**
         * 最长间隔30s
         */
        const val MAX_INTERVAL: Int = 30000
    }
}
