package com.zyf.oa.domain

import com.zyf.common.code.SchemaEnum
import com.zyf.common.enums.NoticeVisibleRangeDataTypeEnum
import com.zyf.common.valid.CheckEnum
import jakarta.validation.constraints.NotNull

class NoticeVisibleRangeForm {
    @SchemaEnum(NoticeVisibleRangeDataTypeEnum::class)
    @CheckEnum(value = NoticeVisibleRangeDataTypeEnum::class, required = true, message = "数据类型错误")
    var dataType: Int? = null

    /** 员工/部门id  */
    var dataId: @NotNull(message = "员工/部门id不能为空") String? = null
}