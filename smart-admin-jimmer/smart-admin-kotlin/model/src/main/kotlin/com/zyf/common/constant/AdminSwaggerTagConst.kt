package com.zyf.common.constant


/**
 * swagger
 *
 * @Author 1024创新实验室:罗伊
 * @Date 2022-01-07 18:59:22
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class AdminSwaggerTagConst : SwaggerTagConst() {
    object Business {
        const val MANAGER_CATEGORY: String = "ERP进销存-分类管理"

        const val MANAGER_GOODS: String = "ERP进销存-商品管理"

        const val OA_BANK: String = "OA办公-银行卡信息"

        const val OA_ENTERPRISE: String = "OA办公-企业"

        const val OA_INVOICE: String = "OA办公-发票信息"

        const val OA_NOTICE: String = "OA办公-通知公告"
    }


    object System {
        const val SYSTEM_LOGIN: String = "系统-员工登录"

        const val SYSTEM_EMPLOYEE: String = "系统-员工管理"

        const val SYSTEM_DEPARTMENT: String = "系统-部门管理"

        const val SYSTEM_MENU: String = "系统-菜单"

        const val SYSTEM_DATA_SCOPE: String = "系统-系统-数据范围"

        const val SYSTEM_ROLE: String = "系统-角色"

        const val SYSTEM_ROLE_DATA_SCOPE: String = "系统-角色-数据范围"

        const val SYSTEM_ROLE_EMPLOYEE: String = "系统-角色-员工"

        const val SYSTEM_ROLE_MENU: String = "系统-角色-菜单"

        const val SYSTEM_POSITION: String = "系统-职务管理"
    }
}
