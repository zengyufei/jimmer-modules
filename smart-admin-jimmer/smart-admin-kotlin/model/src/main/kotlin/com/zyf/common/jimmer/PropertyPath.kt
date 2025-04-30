package com.zyf.common.jimmer

import com.zyf.employee.Employee
import com.zyf.oa.Enterprise
import kotlin.reflect.KProperty1

// 1. 定义一个表示属性路径的类
// PropertyPath<Root, Target> 表示从 Root 类型到 Target 属性值的路径
class PropertyPath(val properties: List<KProperty1<*, *>>) {
    // 可以选择覆盖 toString() 来方便查看路径，但运行时应使用 properties 列表
    override fun toString(): String {
        return properties.joinToString(".") { it.name }
    }

    // 可以提供一个方法来获取属性列表供后续处理 (例如，反射获取值)
    fun getPropertiesList(): List<KProperty1<*, *>> = properties
}

// 2. 创建辅助函数/中缀函数来构建 PropertyPath

// 将单个 KProperty1 转换为 PropertyPath 的起始
// 例如: Enterprise::enterpriseName.asPath()
fun KProperty1<*, *>.asPath(): PropertyPath {
    return PropertyPath(listOf(this))
}

// 中缀函数：将一个 KProperty1 追加到现有的 PropertyPath 后面
// 例如: Enterprise::create.asPath().dot(Employee::actualName)
infix fun PropertyPath.dot(
    other: KProperty1<*, *>
): PropertyPath {
    // 检查类型是否匹配是 KProperty1 的职责，我们只需要组合列表
    return PropertyPath(this.properties + other)
}

// 中缀函数：将一个 KProperty1 追加到另一个 KProperty1 后面，创建一个 PropertyPath
// 例如: Enterprise::create.dot(Employee::actualName)
infix fun KProperty1<*, *>.dot(
    other: KProperty1<*, *>
): PropertyPath {
    // 检查类型是否匹配是 KProperty1 的职责，我们只需要组合列表
    return PropertyPath(listOf(this, other))
}


//fun main() {
//    val outVos = mutableSetOf<OutVo>()
//
//    outVos.add(OutVo(Enterprise::enterpriseName))
//    outVos.add(OutVo(Enterprise::provinceName))
//    outVos.add(OutVo(Enterprise::create dot Employee::actualName))
//    outVos.add(OutVo(
//        Enterprise::create
//        .dot(Employee::enterprise)
//        .dot(Enterprise::enterpriseName))
//    )
//    outVos.add(
//        OutVo(
//            Enterprise::create dot
//                    Employee::enterprise dot
//                    Enterprise::create dot
//                    Employee::actualName
//        )
//    )
//    outVos.add(OutVo(
//        Enterprise::create dot
//            Employee::enterprise dot
//            Enterprise::create dot
//            Employee::enterprise dot
//            Enterprise::enterpriseName
//    ))
//
//    for (stateVo in outVos) {
//        println(stateVo.columnKey)
//    }
//}