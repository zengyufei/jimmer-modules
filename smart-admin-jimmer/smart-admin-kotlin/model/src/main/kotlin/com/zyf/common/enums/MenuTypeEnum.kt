package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum


/**
 * 菜单类型枚举
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
enum class MenuTypeEnum(
    @JsonValue override val value: Int,
    override val desc: String
) : BaseEnum {
    /**
     * 目录
     */
    CATALOG(1, "目录"),

    /**
     * 菜单
     */
    MENU(2, "菜单"),

    /**
     * 功能点
     */
    POINTS(3, "功能点"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: Int): MenuTypeEnum {
            return when (value) {
                CATALOG.value -> CATALOG
                MENU.value -> MENU
                POINTS.value -> POINTS
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
