package com.zyf.cfg

import com.zyf.oa.Enterprise
import com.zyf.oa.EnterpriseProps
import org.babyfish.jimmer.sql.exception.SaveException.IllegalTargetId
import org.babyfish.jimmer.sql.runtime.ExceptionTranslator
import org.springframework.stereotype.Component

@Component
class IllegalTargetIdExceptionTranslator :
    ExceptionTranslator<IllegalTargetId> {

    override fun translate(
        exception: IllegalTargetId,
        args: ExceptionTranslator.Args
    ): Exception? =
        when {
            exception.prop == EnterpriseProps.CREATE_ID.unwrap() ->
                throw IllegalArgumentException(
                    "无法为企业设置非法的关联创建人ID: ${
                        exception.targetIds
                    }"
                )
            else ->
                null //不做处理，也可以写作`exception`
        }
}