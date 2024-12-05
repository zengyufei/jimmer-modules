package com.zyf.department.domain

import com.zyf.employee.Department
import com.zyf.service.dto.DepartmentVO


/**
 * 部门
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-01-12 20:37:48
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class DepartmentTreeVO(base: Department) : DepartmentVO(base) {
    /** 同级上一个元素id  */
    var preId: String? = null

    /** 同级下一个元素id  */
    var nextId: String? = null

    /** 子部门  */
    var children: MutableList<DepartmentTreeVO>? = null

    /** 自己和所有递归子部门的id集合  */
    var selfAndAllChildrenIdList: MutableList <String>? = null
}
