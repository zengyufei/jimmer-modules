package com.zyf.login.domain

import com.zyf.common.enums.MenuPermsTypeEnum
import com.zyf.common.enums.MenuTypeEnum
import com.zyf.common.valid.CheckEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

/**
 * 菜单基础
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-03-06 22:04:37
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
open class MenuBaseForm {
    /** 菜单名称  */
    val menuName: @NotBlank(message = "菜单名称不能为空") @Length(
        max = 30,
        message = "菜单名称最多30个字符"
    ) String? = null

    @CheckEnum(value = MenuTypeEnum::class, message = "类型错误")
    val menuType: Int? = null

    /** 父菜单ID 无上级可传0  */
    val parentId: @NotNull(message = "父菜单ID不能为空") Long? = null

    /** 显示顺序  */
    val sort: Int? = null

    /** 路由地址  */
    val path: String? = null

    /** 组件路径  */
    val component: String? = null

    /** 是否为外链  */
    val frameFlag: @NotNull(message = "是否为外链不能为空") Boolean? = null

    /** 外链地址  */
    val frameUrl: String? = null

    /** 是否缓存  */
    val cacheFlag: @NotNull(message = "是否缓存不能为空") Boolean? = null

    /** 显示状态  */
    val visibleFlag: @NotNull(message = "显示状态不能为空") Boolean? = null

    /** 禁用状态  */
    val disabledFlag: @NotNull(message = "禁用状态不能为空") Boolean? = null

    @CheckEnum(value = MenuPermsTypeEnum::class, message = "权限类型")
    val permsType: Int? = null

    /** 前端权限字符串  */
    val webPerms: String? = null

    /** 后端端权限字符串  */
    val apiPerms: String? = null

    /** 菜单图标  */
    val icon: String? = null

    /** 功能点关联菜单ID  */
    val contextMenuId: Long? = null
}
