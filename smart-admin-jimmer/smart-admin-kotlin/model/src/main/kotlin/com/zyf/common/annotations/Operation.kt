package com.zyf.common.annotations

import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class Operation(
    val method: String = "",
    val tags: Array<String> = [],
    val summary: String = "",
    val description: String = "",
    val operationId: String = "",
    val deprecated: Boolean = false,
    val hidden: Boolean = false,
    val ignoreJsonView: Boolean = false
)
