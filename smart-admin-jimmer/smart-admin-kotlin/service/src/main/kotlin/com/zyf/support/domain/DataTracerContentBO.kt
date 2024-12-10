package com.zyf.support.domain

import org.babyfish.jimmer.meta.ImmutableProp

/**
 * 变动内容
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-07-23 19:38:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class DataTracerContentBO {
    /**
     * 变动字段
     */
    var field: ImmutableProp? = null

    /**
     * 变动字段的值
     */
    var fieldValue: Any? = null

    /**
     * 变动字段描述
     */
    var fieldDesc: String? = null

    /**
     * 变动内容
     */
    var fieldContent: String? = null
}
