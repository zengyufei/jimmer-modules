package com.zyf.support.domain

import com.zyf.common.enums.SerialNumberRuleTypeEnum


/**
 * 单据序列号 信息
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class SerialNumberInfoBO {
    /**
     * 主键id
     *
     * @see SerialNumberIdEnum
     */
    var serialNumberId: String? = null

    /**
     * 业务
     */
    var businessName: String? = null

    /**
     * 格式
     */
    var format: String? = null

    /**
     * 生成规则
     *
     * @see SerialNumberRuleTypeEnum
     */
    var ruleType: String? = null


    /**
     * 初始值
     */
    var initNumber: Long? = null

    /**
     * 步长随机数范围
     */
    var stepRandomRange: Int? = null

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 规则枚举
     */
    var serialNumberRuleTypeEnum: SerialNumberRuleTypeEnum? = null


    /**
     * 存在[nnnnnn]中 n 的数量
     */
    var numberCount: Int? = null

    /**
     * [nnnnnn] 的格式（主要用于替换）
     */
    var numberFormat: String? = null

    /**
     * 是否存在年份
     */
    var haveYearFlag: Boolean? = null

    /**
     * 是否存在月份
     */
    var haveMonthFlag: Boolean? = null

    /**
     * 是否存在 月
     */
    var haveDayFlag: Boolean? = null
}
