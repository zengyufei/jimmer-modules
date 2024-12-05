package com.zyf.system.domain

import com.zyf.service.dto.DataScopeViewTypeVO

/**
 * 数据范围
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class DataScopeAndViewTypeVO {
    /** 数据范围类型  */
    var dataScopeType: Int? = null

    /** 数据范围名称  */
    var dataScopeTypeName: String? = null

    /** 描述  */
    var dataScopeTypeDesc: String? = null

    /** 顺序  */
    var dataScopeTypeSort: Int? = null

    /** 可见范围列表  */
    var viewTypeList: List<DataScopeViewTypeVO>? = null
}
