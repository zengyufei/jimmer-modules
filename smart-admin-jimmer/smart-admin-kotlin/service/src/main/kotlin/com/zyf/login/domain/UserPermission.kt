package com.zyf.login.domain

class UserPermission {
    /**
     * 权限列表
     */
    val permissionList = mutableSetOf<String>()

    /**
     * 角色列表
     */
    val roleList = mutableSetOf<String>()
}
