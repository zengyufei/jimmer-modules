package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import com.zyf.common.constant.StringConst

/**
 * 单据序列号 周期
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
enum class SerialNumberRuleTypeEnum(
    @JsonValue override val value: String,
    val regex: String,
    override val desc: String
) : BaseEnum {
    /**
     * 没有周期
     */
    NONE(StringConst.EMPTY, "", "没有周期"),

    /**
     * 年周期
     */
    YEAR("[yyyy]", "\\[yyyy\\]", "年"),

    /**
     * 月周期
     */
    MONTH("[mm]", "\\[mm\\]", "年月"),

    /**
     * 日周期
     */
    DAY("[dd]", "\\[dd\\]", "年月日");
}
