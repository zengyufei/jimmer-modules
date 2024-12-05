package com.zyf.common.code

/**
 * 未预期的错误码（即发生了不可能发生的事情，此类返回码应该高度重视）
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021/09/27 22:10:46
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
enum class UnexpectedErrorCode(override val code: Int, override val msg: String) : ErrorCode {
    /**
     * 业务错误
     */
    BUSINESS_HANDING(20001, "呃~ 业务繁忙，请稍后重试"),

    /**
     * id错误
     */
    PAY_ORDER_ID_ERROR(20002, "付款单id发生了异常，请联系技术人员排查"),
    ;

    override val level: String = ErrorCode.LEVEL_UNEXPECTED
}
