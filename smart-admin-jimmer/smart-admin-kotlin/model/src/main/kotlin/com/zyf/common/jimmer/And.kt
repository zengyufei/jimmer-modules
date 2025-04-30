package com.zyf.common.jimmer

import kotlin.reflect.KProperty1

class And(vararg conditions: InputForm?) {
    val conditions = mutableListOf<InputForm>()

    init {
        conditions.forEach {
            it?.let {
                this.conditions.add(it)
            }
        }
    }

    fun toMutableSet(): MutableSet<InputForm> {
        return conditions.toMutableSet()
    }
    fun add(inputForm: InputForm?): And {
        inputForm?.let{
            conditions.add(it)
        }
        return this
    }
    // 使用这种形式直接接收字段名称，并存储
    operator fun InputForm?.unaryPlus() {
        this?.let {
            conditions.add(it)
        }
    }
}

// 定义 DSL 构建器
fun And(block: And.() -> Unit): And {
    val and = And()
    and.block() // 将 DSL 的逻辑应用到 OutVo
    return and
}