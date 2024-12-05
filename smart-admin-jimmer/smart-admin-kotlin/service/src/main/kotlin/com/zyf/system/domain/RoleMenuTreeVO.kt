package com.zyf.system.domain

import com.zyf.service.dto.MenuSimpleTreeVO

/**
 * 角色菜单树
 *
 * @Author 1024创新实验室: 善逸
 * @Date 2022-04-08 21:53:04
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class RoleMenuTreeVO {
    /** 角色ID  */
    var roleId: String? = null

    /** 菜单列表  */
    var menuTreeList: List<MenuSimpleTreeVO>? = null

    /** 选中的菜单ID  */
    var selectedMenuId: List<String>? = null
}
