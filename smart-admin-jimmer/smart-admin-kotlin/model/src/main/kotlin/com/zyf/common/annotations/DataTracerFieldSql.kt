package com.zyf.common.annotations


/**
 * 支持查询sql
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class DataTracerFieldSql(
    /**
     * 关联字段名称
     * @return
     */
    val relateColumn: String = "id",
    /**
     * 关联显示的字段
     * @return
     */
    val relateDisplayColumn: String = "",
    // /**
    //  * 是否关联字段查询Mapper
    //  * @return
    //  */
    // val relateMapper: KClass<out BaseMapper?> = BaseMapper::class
)
