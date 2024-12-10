package com.zyf.common.convert

import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import com.zyf.common.base.BaseEnum

/**
 * 枚举值转换器
 */
class ExcelEnum<T> : Converter<T?> {

    override fun supportJavaTypeKey(): Class<*> {
        return Any::class.java
    }

    override fun supportExcelTypeKey(): CellDataTypeEnum {
        return CellDataTypeEnum.STRING
    }

    override fun convertToJavaData(cellData: ReadCellData<*>, contentProperty: ExcelContentProperty, globalConfiguration: GlobalConfiguration): T? {
        val type = contentProperty.field.type
        if (type.isEnum && BaseEnum::class.java.isAssignableFrom(type)) {
            for (enumConstant in type.enumConstants) {
                if (enumConstant is BaseEnum) {
                    if (enumConstant.desc == cellData.stringValue) {
                        return enumConstant as T
                    }
                } else {
                    throw IllegalArgumentException(
                        String.format(
                            "枚举类型必须实现%s接口",
                            BaseEnum::class.java.name
                        ))
                }
            }
        }
        return null
    }

    override fun convertToExcelData(value: T?, contentProperty: ExcelContentProperty, globalConfiguration: GlobalConfiguration): WriteCellData<*> {
        if (value is BaseEnum) {
            return WriteCellData<Any>(value.desc)
        }
        throw IllegalArgumentException(
            String.format(
                "转换枚举失败: %s",
                contentProperty.field.name
            ))
    }
}
