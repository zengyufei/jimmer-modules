package com.zyf.common.domain


import com.zyf.common.code.DataTypeEnum
import com.zyf.common.code.ErrorCode
import com.zyf.common.code.SchemaEnum
import com.zyf.common.code.UserErrorCode

/**
 * 请求返回对象
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-10-31 21:06:11
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class ResponseDTO<T> {
    /** 返回码  */
    var code: Int

    /** 级别  */
    var level: String?

    var msg: String? = null

    var ok: Boolean

    /** 返回数据  */
    var data: T? = null

    /** 数据类型  */
    @SchemaEnum(value = DataTypeEnum::class, desc = "数据类型")
    var dataType: Int

    constructor(code: Int, level: String?, ok: Boolean, msg: String?, data: T) {
        this.code = code
        this.level = level
        this.ok = ok
        this.msg = msg
        this.data = data
        this.dataType = DataTypeEnum.NORMAL.value
    }

    constructor(code: Int, level: String?, ok: Boolean, msg: String?) {
        this.code = code
        this.level = level
        this.ok = ok
        this.msg = msg
        this.dataType = DataTypeEnum.NORMAL.value
    }

    constructor(errorCode: ErrorCode, ok: Boolean, msg: String?, data: T) {
        this.code = errorCode.code
        this.level = errorCode.level
        this.ok = ok
        if (msg?.isNotBlank() == true) {
            this.msg = msg
        } else {
            this.msg = errorCode.msg
        }
        this.data = data
        this.dataType = DataTypeEnum.NORMAL.value
    }

    companion object {
        const val OK_CODE: Int = 0

        const val OK_MSG: String = "操作成功"

        fun <T> ok(): ResponseDTO<T?> {
            return ResponseDTO(OK_CODE, null, true, OK_MSG, null)
        }

        fun <T> ok(data: T): ResponseDTO<T> {
            return ResponseDTO(OK_CODE, null, true, OK_MSG, data)
        }

        fun <T> okMsg(msg: String?): ResponseDTO<T?> {
            return ResponseDTO(OK_CODE, null, true, msg, null)
        }

        // -------------------------------------------- 最常用的 用户参数 错误码 --------------------------------------------
        fun <T> userErrorParam(): ResponseDTO<T?> {
            return ResponseDTO<T?>(UserErrorCode.PARAM_ERROR, false, null, null)
        }


        fun <T> userErrorParam(msg: String?): ResponseDTO<T?> {
            return ResponseDTO<T?>(UserErrorCode.PARAM_ERROR, false, msg, null)
        }

        // -------------------------------------------- 错误码 --------------------------------------------
        fun <T> error(errorCode: ErrorCode): ResponseDTO<T?> {
            return ResponseDTO(errorCode, false, null, null)
        }

        fun <T> error(errorCode: ErrorCode, ok: Boolean): ResponseDTO<T?> {
            return ResponseDTO(errorCode, ok, null, null)
        }

        fun <T> error(responseDTO: ResponseDTO<*>): ResponseDTO<T?> {
            return ResponseDTO<T?>(
                responseDTO.code,
                responseDTO.level,
                responseDTO.ok,
                responseDTO.msg,
                null
            )
        }

        fun <T> error(errorCode: ErrorCode, msg: String?): ResponseDTO<T?> {
            return ResponseDTO(errorCode, false, msg, null)
        }

        fun <T> errorData(errorCode: ErrorCode, data: T): ResponseDTO<T> {
            return ResponseDTO(errorCode, false, null, data)
        }
    }
}
