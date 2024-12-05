package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum

enum class ConfigKeyEnum(
    @JsonValue override val value: String,
    override val desc: String
) : BaseEnum {

    /**
     * 万能密码
     */
    SUPER_PASSWORD("super_password", "万能密码"),

    LEVEL3_PROTECT_CONFIG("level3_protect_config", "三级等保配置"),
    ;

    companion object {

        @JsonCreator
        fun deserialize(value: String): ConfigKeyEnum {
            return when (value) {
                SUPER_PASSWORD.value -> SUPER_PASSWORD
                LEVEL3_PROTECT_CONFIG.value -> LEVEL3_PROTECT_CONFIG
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
