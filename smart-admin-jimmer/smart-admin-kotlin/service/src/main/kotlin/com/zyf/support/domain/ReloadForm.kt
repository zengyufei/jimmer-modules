package com.zyf.support.domain

import jakarta.validation.constraints.NotBlank

/**
 * reload (内存热加载、钩子等)
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class ReloadForm {
    
    /** 标签 */
    var tag: @NotBlank(message = "标签不能为空") String? = null

    /** 状态标识 */
    var identification: @NotBlank(message = "状态标识不能为空") String? = null

    /** 参数 */
    var args: String? = null
}
