package com.zyf.common.code

enum class SystemErrorCode(override val code: Int, override val msg: String) : ErrorCode {
    /**
     * 系统错误
     */
    SYSTEM_ERROR(10001, "系统似乎出现了点小问题"),
    ;

    override val level: String = ErrorCode.LEVEL_SYSTEM
}
