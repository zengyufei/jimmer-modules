package com.zyf.cfg.json.datamasking

import cn.hutool.core.util.DesensitizedUtil

/**
 * 脱敏数据类型
 *
 * @Author 1024创新实验室-创始人兼主任:卓大
 * @Date 2024/8/1
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net) ，Since 2012
 */
enum class DataMaskingTypeEnum(val type: DesensitizedUtil.DesensitizedType?, val desc: String) {
    COMMON(null, "通用"),
    PHONE(DesensitizedUtil.DesensitizedType.MOBILE_PHONE, "手机号"),
    CHINESE_NAME(DesensitizedUtil.DesensitizedType.CHINESE_NAME, "中文名"),
    ID_CARD(DesensitizedUtil.DesensitizedType.ID_CARD, "身份证号"),
    FIXED_PHONE(DesensitizedUtil.DesensitizedType.FIXED_PHONE, "座机号"),
    ADDRESS(DesensitizedUtil.DesensitizedType.ADDRESS, "地址"),
    EMAIL(DesensitizedUtil.DesensitizedType.EMAIL, "电子邮件"),
    PASSWORD(DesensitizedUtil.DesensitizedType.PASSWORD, "密码"),
    CAR_LICENSE(DesensitizedUtil.DesensitizedType.CAR_LICENSE, "中国大陆车牌"),
    BANK_CARD(DesensitizedUtil.DesensitizedType.BANK_CARD, "银行卡"),
    USER_ID(DesensitizedUtil.DesensitizedType.USER_ID, "用户id");
}
