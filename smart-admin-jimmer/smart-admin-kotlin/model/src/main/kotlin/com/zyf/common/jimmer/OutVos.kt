package com.zyf.common.jimmer

import com.zyf.employee.Employee
import com.zyf.oa.Enterprise
import kotlin.reflect.KProperty1

// 定义 OutVos 类
class OutVos(vararg items: Any?)  {
    private val itemList: MutableList<Any?> = items.toMutableList() // 将可变参数存储在列表中

    // 重写 toString() 方法来生成期望的字符串输出
    override fun toString(): String {
        return itemList.joinToString(",") { item ->
            when (item) {
                is KProperty1<*, *> -> item.name // 如果是单个属性引用 (KProperty1)，使用它的名字
                is PropertyPath -> item.toString() // 如果是属性路径，使用 PropertyPath 的 toString (它已经实现了链式名字的拼接)
                is String -> item // 如果是字符串，直接使用字符串本身
                else -> item.toString() // 处理其他未知类型（可选，取决于你的需求）
            }
        }
    }
//    fun add(vararg items: Any?) {
//        itemList.addAll(items)
//    }
    fun toMutableSet():MutableSet<OutVo> {
        return itemList.map {
            when (it) {
                is KProperty1<*, *> -> OutVo(it.name)
                is PropertyPath -> OutVo(it)
                is String -> OutVo(it)
                else -> OutVo(it.toString())
            }
        }.toMutableSet()
    }
}
fun main() {

    val outVos1 = OutVos(
        Enterprise::enterpriseName,
        "provinceName",
        Enterprise::create dot Employee::actualName
    )
    println(outVos1) // enterpriseName,provinceName,create.actualName
}