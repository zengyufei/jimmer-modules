package com.zyf.common.jimmer

class Or(vararg conditions: InputForm?) {
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
    fun add(inputForm: InputForm?): Or {
        inputForm?.let{
            conditions.add(it)
        }
        return this
    }
}