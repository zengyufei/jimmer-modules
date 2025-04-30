package com.zyf.common.jimmer

import kotlin.reflect.KProperty1

class Sort {

    companion object {
        const val Asc: String = "ASC"
        const val Desc: String = "DESC"
    }

    val columnKey: String
    var order: String

    constructor(columnKey: String, order: String) {
        this.columnKey = columnKey
        this.order = order
    }

    constructor(columnKey: KProperty1<*, *>, order: String) {
        this.columnKey = columnKey.name
        this.order = order
    }

    constructor(columnKey: PropertyPath, order: String) {
        this.columnKey = columnKey.toString()
        this.order = order
    }
}


infix fun String.by(value: String?): Sort {
    return Sort(this, value ?: Sort.Asc)
}

infix fun PropertyPath.by(value: String?): Sort {
    return Sort(this, value ?: Sort.Asc)
}

infix fun KProperty1<*, *>.by(value: String?): Sort {
    return Sort(this, value ?: Sort.Asc)
}

infix fun Sort.by(value: String?): Sort {
    this.order = value ?: Sort.Asc
    return this
}