package com.zyf.common.jimmer

import kotlin.reflect.KProperty1

class OutVo {
    val columnKey: String

    constructor( columnKey: String) {
        this.columnKey = columnKey
    }
    constructor(columnKey: KProperty1<*, *>) {
        this.columnKey = columnKey.name
    }
    constructor(columnKey: PropertyPath) {
        this.columnKey = columnKey.toString()
    }

}