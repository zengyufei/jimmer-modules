package com.zyf.common.enums

import com.zyf.common.base.BaseEnum


/**
 * 系统环境枚举类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2020-10-15 22:45:04
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
enum class SystemEnvironmentEnum(override val value: String, override val desc: String) : BaseEnum {
    /**
     * dev
     */
    DEV(SystemEnvironmentNameConst.DEV, "开发环境"),

    /**
     * test
     */
    TEST(SystemEnvironmentNameConst.TEST, "测试环境"),

    /**
     * pre
     */
    PRE(SystemEnvironmentNameConst.PRE, "预发布环境"),

    /**
     * prod
     */
    PROD(SystemEnvironmentNameConst.PROD, "生产环境");


    object SystemEnvironmentNameConst {
        const val DEV: String = "dev"
        const val TEST: String = "test"
        const val PRE: String = "pre"
        const val PROD: String = "prod"
    }
}
