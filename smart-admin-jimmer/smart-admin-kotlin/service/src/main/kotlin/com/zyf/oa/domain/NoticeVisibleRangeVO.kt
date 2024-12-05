package com.zyf.oa.domain

import com.zyf.common.code.SchemaEnum
import com.zyf.common.enums.NoticeVisibleRangeDataTypeEnum

class NoticeVisibleRangeVO {

    @SchemaEnum(NoticeVisibleRangeDataTypeEnum::class)
    var dataType: Int? = null

    /** 员工/部门id */
    var dataId: String? = null

    /** 员工/部门 名称 */
    var dataName: String? = null
}
