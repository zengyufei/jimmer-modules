package com.zyf.cfg

import com.zyf.employee.Employee
import com.zyf.oa.Enterprise
import com.zyf.support.ChangeLog
import com.zyf.support.Config
import com.zyf.support.DictKey
import com.zyf.system.Menu
import com.zyf.system.Role
import org.babyfish.jimmer.sql.exception.SaveException.NotUnique
import org.babyfish.jimmer.sql.kt.get
import org.babyfish.jimmer.sql.kt.isMatched
import org.babyfish.jimmer.sql.runtime.ExceptionTranslator
import org.springframework.stereotype.Component

@Component
class NotUniqueExceptionTranslator() : ExceptionTranslator<NotUnique> {
    override fun translate(
        exception: NotUnique,
        args: ExceptionTranslator.Args
    ): Exception? =
        when {
            exception.isMatched(Enterprise::enterpriseName) ->
                throw IllegalArgumentException(
                    "${exception[Enterprise::enterpriseName]}的企业已经存在！"
                )
            exception.isMatched(Employee::loginName) ->
                throw IllegalArgumentException(
                    "${exception[Employee::loginName]}的登录用户名已存在！"
                )
            exception.isMatched(ChangeLog::version) ->
                throw IllegalArgumentException(
                    "${exception[ChangeLog::version]}的系统更新日志版本已存在！"
                )
            exception.isMatched(DictKey::keyCode) ->
                throw IllegalArgumentException(
                    "${exception[DictKey::keyCode]}的字典编码已存在！"
                )
            exception.isMatched(Config::configKey) ->
                throw IllegalArgumentException(
                    "${exception[Config::configKey]}的参数key已存在！"
                )
            exception.isMatched(Role::roleCode, Role::roleName) ->
                throw IllegalArgumentException(
                    "编码为${
                        exception[Role::roleCode]
                    }且名称为${
                        exception[Role::roleName]
                    }的角色已经存在！"
                )
            exception.isMatched(Menu::parentId, Menu::menuName) ->
                throw IllegalArgumentException(
                    "上级为${
                        exception[Menu::parentId]
                    }且名称为${
                        exception[Menu::menuName]
                    }的菜单已经存在！"
                )
            else ->
                null //不做处理，也可以写作`exception`
        }
}

