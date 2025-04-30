package com.zyf.common.jimmer

class Sorts(vararg conditions: Sort?) {
    private val conditions = mutableListOf<Sort>()

    init {
        conditions.forEach {
            it?.let {
                this.conditions.add(it)
            }
        }
    }

    fun toMutableSet(): MutableSet<Sort> {
        return conditions.toMutableSet()
    }
    fun add(vararg items: Sort) {
        conditions.addAll(items)
    }
    fun add(sort: Sort?): Sorts {
        sort?.let{
            conditions.add(it)
        }
        return this
    }
}