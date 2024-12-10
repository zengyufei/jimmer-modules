package com.zyf.support.domain

import com.zyf.service.dto.TableColumnItemForm
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

class TableColumnUpdateForm {

    @NotNull(message = "表id不能为空")
    lateinit var tableId: String

    @NotEmpty(message = "请上传列")
    val columnList: List<TableColumnItemForm>? = null

}