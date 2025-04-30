package com.zyf.common.jimmer

import kotlin.reflect.KProperty1

class InputForm {
    val operator: InputOperator
    val columnKey: String
    val value: Any?

    constructor(operator: InputOperator, columnKey: String, value: Any?) {
        this.operator = operator
        this.columnKey = columnKey
        this.value = value
    }

    constructor(operator: InputOperator, columnKey: KProperty1<*, *>, value: Any?) {
        this.operator = operator
        this.columnKey = columnKey.name
        this.value = value
    }

    constructor(operator: InputOperator, columnKey: PropertyPath, value: Any?) {
        this.operator = operator
        this.columnKey = columnKey.toString()
        this.value = value
    }
}

// 定义中缀扩展函数
// infix fun String.函数名(参数名: 参数类型): 返回类型 { ... }

/**
 * 创建一个表示“等于”搜索条件的 StateSearchForm
 * 使用方法： "fieldName" eq value
 */
infix fun String.eq(value: Any?): InputForm = InputForm(InputOperator.EQ, this, value)
infix fun String.`eq?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.EQ, this, value) }
infix fun String.ne(value: Any?): InputForm = InputForm(InputOperator.NE, this, value)
infix fun String.`ne?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NE, this, value) }
infix fun String.like(value: Any?): InputForm = InputForm(InputOperator.LIKE, this, value)
infix fun String.`like?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LIKE, this, value) }
infix fun String.gt(value: Any?): InputForm = InputForm(InputOperator.GT, this, value)
infix fun String.`gt?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.GT, this, value) }
infix fun String.ge(value: Any?): InputForm = InputForm(InputOperator.GE, this, value)
infix fun String.`ge?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.GE, this, value) }
infix fun String.lt(value: Any?): InputForm = InputForm(InputOperator.LT, this, value)
infix fun String.`lt?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LT, this, value) }
infix fun String.le(value: Any?): InputForm = InputForm(InputOperator.LE, this, value)
infix fun String.`le?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LE, this, value) }
infix fun String.valueIn(value: Any?): InputForm = InputForm(InputOperator.IN, this, value)
infix fun String.`valueIn?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IN, this, value) }
infix fun String.valueNotIn(value: Any?): InputForm = InputForm(InputOperator.NOT_IN, this, value)
infix fun String.`valueNotIn?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NOT_IN, this, value) }
infix fun String.between(value: Any?): InputForm = InputForm(InputOperator.BETWEEN, this, value)
infix fun String.`between?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.BETWEEN, this, value) }
infix fun String.notBetween(value: Any?): InputForm = InputForm(InputOperator.NOT_BETWEEN, this, value)
infix fun String.`notBetween?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NOT_BETWEEN, this, value) }
infix fun String.isNull(value: Any?): InputForm = InputForm(InputOperator.IS_NULL, this, value)
infix fun String.`isNull?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IS_NULL, this, value) }
infix fun String.isNotNull(value: Any?): InputForm = InputForm(InputOperator.IS_NOT_NULL, this, value)
infix fun String.`isNotNull?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IS_NOT_NULL, this, value) }
infix fun String.startsWith(value: Any?): InputForm = InputForm(InputOperator.STARTS_WITH, this, value)
infix fun String.`startsWith?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.STARTS_WITH, this, value) }
infix fun String.endsWith(value: Any?): InputForm = InputForm(InputOperator.ENDS_WITH, this, value)
infix fun String.`endsWith?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.ENDS_WITH, this, value) }

// ======================================================================================================================
// ======================================================================================================================
// ======================================================================================================================


infix fun PropertyPath.eq(value: Any?): InputForm = InputForm(InputOperator.EQ, this, value)
infix fun PropertyPath.`eq?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.EQ, this, value) }
infix fun PropertyPath.ne(value: Any?): InputForm = InputForm(InputOperator.NE, this, value)
infix fun PropertyPath.`ne?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NE, this, value) }
infix fun PropertyPath.like(value: Any?): InputForm = InputForm(InputOperator.LIKE, this, value)
infix fun PropertyPath.`like?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LIKE, this, value) }
infix fun PropertyPath.gt(value: Any?): InputForm = InputForm(InputOperator.GT, this, value)
infix fun PropertyPath.`gt?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.GT, this, value) }
infix fun PropertyPath.ge(value: Any?): InputForm = InputForm(InputOperator.GE, this, value)
infix fun PropertyPath.`ge?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.GE, this, value) }
infix fun PropertyPath.lt(value: Any?): InputForm = InputForm(InputOperator.LT, this, value)
infix fun PropertyPath.`lt?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LT, this, value) }
infix fun PropertyPath.le(value: Any?): InputForm = InputForm(InputOperator.LE, this, value)
infix fun PropertyPath.`le?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LE, this, value) }
infix fun PropertyPath.valueIn(value: Any?): InputForm = InputForm(InputOperator.IN, this, value)
infix fun PropertyPath.`valueIn?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IN, this, value) }
infix fun PropertyPath.valueNotIn(value: Any?): InputForm = InputForm(InputOperator.NOT_IN, this, value)
infix fun PropertyPath.`valueNotIn?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NOT_IN, this, value) }
infix fun PropertyPath.between(value: Any?): InputForm = InputForm(InputOperator.BETWEEN, this, value)
infix fun PropertyPath.`between?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.BETWEEN, this, value) }
infix fun PropertyPath.notBetween(value: Any?): InputForm = InputForm(InputOperator.NOT_BETWEEN, this, value)
infix fun PropertyPath.`notBetween?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NOT_BETWEEN, this, value) }
infix fun PropertyPath.isNull(value: Any?): InputForm = InputForm(InputOperator.IS_NULL, this, value)
infix fun PropertyPath.`isNull?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IS_NULL, this, value) }
infix fun PropertyPath.isNotNull(value: Any?): InputForm = InputForm(InputOperator.IS_NOT_NULL, this, value)
infix fun PropertyPath.`isNotNull?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IS_NOT_NULL, this, value) }
infix fun PropertyPath.startsWith(value: Any?): InputForm = InputForm(InputOperator.STARTS_WITH, this, value)
infix fun PropertyPath.`startsWith?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.STARTS_WITH, this, value) }
infix fun PropertyPath.endsWith(value: Any?): InputForm = InputForm(InputOperator.ENDS_WITH, this, value)
infix fun PropertyPath.`endsWith?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.ENDS_WITH, this, value) }

// ======================================================================================================================
// ======================================================================================================================
// ======================================================================================================================

infix fun KProperty1<*, *>.eq(value: Any?): InputForm = InputForm(InputOperator.EQ, this, value)
infix fun KProperty1<*, *>.`eq?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.EQ, this, value) }
infix fun KProperty1<*, *>.ne(value: Any?): InputForm = InputForm(InputOperator.NE, this, value)
infix fun KProperty1<*, *>.`ne?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NE, this, value) }
infix fun KProperty1<*, *>.like(value: Any?): InputForm = InputForm(InputOperator.LIKE, this, value)
infix fun KProperty1<*, *>.`like?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LIKE, this, value) }
infix fun KProperty1<*, *>.gt(value: Any?): InputForm = InputForm(InputOperator.GT, this, value)
infix fun KProperty1<*, *>.`gt?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.GT, this, value) }
infix fun KProperty1<*, *>.ge(value: Any?): InputForm = InputForm(InputOperator.GE, this, value)
infix fun KProperty1<*, *>.`ge?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.GE, this, value) }
infix fun KProperty1<*, *>.lt(value: Any?): InputForm = InputForm(InputOperator.LT, this, value)
infix fun KProperty1<*, *>.`lt?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LT, this, value) }
infix fun KProperty1<*, *>.le(value: Any?): InputForm = InputForm(InputOperator.LE, this, value)
infix fun KProperty1<*, *>.`le?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.LE, this, value) }
infix fun KProperty1<*, *>.valueIn(value: Any?): InputForm = InputForm(InputOperator.IN, this, value)
infix fun KProperty1<*, *>.`valueIn?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IN, this, value) }
infix fun KProperty1<*, *>.valueNotIn(value: Any?): InputForm = InputForm(InputOperator.NOT_IN, this, value)
infix fun KProperty1<*, *>.`valueNotIn?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NOT_IN, this, value) }
infix fun KProperty1<*, *>.between(value: Any?): InputForm = InputForm(InputOperator.BETWEEN, this, value)
infix fun KProperty1<*, *>.`between?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.BETWEEN, this, value) }
infix fun KProperty1<*, *>.notBetween(value: Any?): InputForm = InputForm(InputOperator.NOT_BETWEEN, this, value)
infix fun KProperty1<*, *>.`notBetween?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.NOT_BETWEEN, this, value) }
infix fun KProperty1<*, *>.isNull(value: Any?): InputForm = InputForm(InputOperator.IS_NULL, this, value)
infix fun KProperty1<*, *>.`isNull?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IS_NULL, this, value) }
infix fun KProperty1<*, *>.isNotNull(value: Any?): InputForm = InputForm(InputOperator.IS_NOT_NULL, this, value)
infix fun KProperty1<*, *>.`isNotNull?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.IS_NOT_NULL, this, value) }
infix fun KProperty1<*, *>.startsWith(value: Any?): InputForm = InputForm(InputOperator.STARTS_WITH, this, value)
infix fun KProperty1<*, *>.`startsWith?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.STARTS_WITH, this, value) }
infix fun KProperty1<*, *>.endsWith(value: Any?): InputForm = InputForm(InputOperator.ENDS_WITH, this, value)
infix fun KProperty1<*, *>.`endsWith?`(value: Any?): InputForm? = value?.let { InputForm(InputOperator.ENDS_WITH, this, value) }
