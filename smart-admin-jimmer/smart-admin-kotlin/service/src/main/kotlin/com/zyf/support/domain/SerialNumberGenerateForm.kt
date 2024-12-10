package com.zyf.support.domain

import jakarta.validation.constraints.NotNull

/**
 * 单据序列号 生成表单
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class SerialNumberGenerateForm {
    /** 单号id */
    var serialNumberId: @NotNull(message = "单号id不能为空") String? = null

    /** 生成的数量 */
    var count: @NotNull(message = "生成的数量") Int? = null
}
