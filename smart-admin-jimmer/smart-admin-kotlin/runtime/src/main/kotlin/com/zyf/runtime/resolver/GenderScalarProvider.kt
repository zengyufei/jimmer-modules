//package com.zyf.runtime.resolver
//
//import com.zyf.common.enums.GenderEnum
//import com.zyf.common.utils.SmartEnumUtil
//import org.babyfish.jimmer.sql.runtime.ScalarProvider
//import org.springframework.stereotype.Component
//
//@Component
//class GenderScalarProvider : ScalarProvider<GenderEnum, Int> {
//
//    override fun toScalar(sqlValue: Int): GenderEnum {
//        return SmartEnumUtil.getEnumByValue<GenderEnum>(sqlValue)!!
//    }
//
//    override fun toSql(scalarValue: GenderEnum): Int {
//        return scalarValue.value
//    }
//
//}