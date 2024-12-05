package com.zyf.support.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.json.JSONUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.*
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.utils.SmartBigDecimalUtil
import com.zyf.common.utils.SmartEnumUtil
import com.zyf.support.domain.DataTracerContentBO
import org.babyfish.jimmer.ImmutableObjects
import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Slf4j
@Service
class DataTracerChangeContentService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
//    val applicationContext: ApplicationContext,
    val dictCacheService: DictCacheService
) {
    /**
     * 字段描述缓存
     */
    private val fieldDescCacheMap = ConcurrentHashMap<String, String>()

    /**
     * 类 加注解字段缓存
     */
//    private val fieldMap = ConcurrentHashMap<Class<*>, List<Field>>()

    /**
     * 数据批量对比
     */
//    fun <T> getChangeContent(oldObjectList: List<T>, newObjectList: List<T>): String {
//        if (!valid(oldObjectList, newObjectList)) {
//            return ""
//        }
//
//        val operateType = getOperateType(oldObjectList, newObjectList)
//        var operateContent = ""
//
//        when (operateType) {
//            DataTracerConst.INSERT, DataTracerConst.DELETE -> {
//                operateContent = getObjectListContent(newObjectList)
//                if (operateContent.isEmpty()) {
//                    return ""
//                }
//                return "$operateType:$operateContent"
//            }
//
//            DataTracerConst.UPDATE -> {
//                return getUpdateContentList(oldObjectList, newObjectList)
//            }
//        }
//        return operateContent
//    }

    /**
     * 解析多个对象的变更，删除，新增
     */
//    fun getChangeContent(oldObject: Any?, newObject: Any?): String {
//        if (!valid(oldObject, newObject)) {
//            return ""
//        }
//
//        val operateType = getOperateType(oldObject, newObject)
//        val operateContent = when (operateType) {
//            DataTracerConst.INSERT, DataTracerConst.DELETE -> getAddDeleteContent(newObject)
//            DataTracerConst.UPDATE -> getUpdateContent(oldObject, newObject)
//            else -> ""
//        }
//
//        return if (operateContent.isEmpty()) "" else operateContent
//    }

    /**
     * 解析单个bean的内容
     */
    fun getChangeContentK(obj: Any): String = getAddDeleteContentK(obj)

    /**
     * 获取新增或删除操作内容
     */
    private fun getAddDeleteContentK(obj: Any?): String {
        obj ?: return ""
        val kFields = getKField(obj)
        val beanParseMap = fieldParseK(obj, kFields)
        return getAddDeleteContent(beanParseMap)
    }

    /**
     * 单个对象变动内容
     */
//    private fun <T> getUpdateContentList(oldObjectList: List<T>, newObjectList: List<T>): String {
//        val oldContent = getObjectListContent(oldObjectList)
//        val newContent = getObjectListContent(newObjectList)
//
//        if (oldContent == newContent) {
//            return ""
//        }
//        if (oldContent.isEmpty() && newContent.isEmpty()) {
//            return ""
//        }
//        return "【原数据】:<br/>$oldContent<br/>【新数据】:<br/>$newContent"
//    }

//    /**
//     * 获取一个对象的内容信息
//     */
//    private fun <T> getObjectListContent(objectList: List<T>): String {
//        if (objectList.isEmpty()) {
//            return ""
//        }
//        val fields = getField(objectList[0]!!)
//        val contentList = objectList.map { obj ->
//            val beanParseMap = fieldParse(obj!!, fields)
//            getAddDeleteContent(beanParseMap)
//        }
//        return contentList.joinToString("<br/>")
//    }

    private fun getAddDeleteContent(beanParseMap: Map<String, DataTracerContentBO>): String {
        val contentList = beanParseMap.values.map { dataTracerContentBO ->
            val jsonFlag = JSONUtil.isTypeJSON(dataTracerContentBO.fieldContent)
            val filedDesc = dataTracerContentBO.fieldDesc
            if (jsonFlag) {
                "$filedDesc(请进入详情查看)"
            } else {
                "${dataTracerContentBO.fieldDesc}:${dataTracerContentBO.fieldContent}"
            }
        }

        return contentList.takeIf { it.isNotEmpty() }?.joinToString("<br/>") ?: ""
    }

    /**
     * 获取更新操作内容
     */
//    private fun <T> getUpdateContent(oldObject: T?, newObject: T?): String {
//        if (oldObject == null || newObject == null) return ""
//
//        val fields = getField(oldObject)
//        val oldBeanParseMap = fieldParse(oldObject, fields)
//        val newBeanParseMap = fieldParse(newObject, fields)
//
//        val contentList = oldBeanParseMap.mapNotNull { (fieldName, oldContentBO) ->
//            val newContentBO = newBeanParseMap[fieldName]
//            val oldContent = oldContentBO?.fieldContent ?: ""
//            val newContent = newContentBO?.fieldContent ?: ""
//
//            if (oldContent == newContent) {
//                null
//            } else {
//                val fieldDesc = oldContentBO.fieldDesc
//                if (JSONUtil.isTypeJSON(oldContent) || JSONUtil.isTypeJSON(newContent)) {
//                    "$fieldDesc【进入详情查看】"
//                } else {
//                    "$fieldDesc:由【$oldContent】变更为【$newContent】"
//                }
//            }
//        }
//
//        return contentList.takeIf { it.isNotEmpty() }?.joinToString("<br/>") ?: ""
//    }


    /**
     * 解析bean对象
     */
    private fun fieldParseK(obj: Any, fields: List<ImmutableProp>): Map<String, DataTracerContentBO> {
        if (fields.isEmpty()) {
            return emptyMap()
        }

        return fields.mapNotNull { field ->
            val desc = getFieldDescK(field) ?: return@mapNotNull null
            val dataTracerContentBO = getFieldValueK(field, obj) ?: return@mapNotNull null
            dataTracerContentBO.fieldDesc = desc
            field.name to dataTracerContentBO
        }.toMap()
    }

    /**
     * 获取字段值
     */

    /**
     * 获取字段值
     */
    private fun getFieldValueK(field: ImmutableProp, obj: Any): DataTracerContentBO? {
        val fieldValue = try {
            ImmutableObjects.get(obj, field)
        } catch (e: Exception) {
            log.error("bean operate log: reflect field value error ${field.name}")
            return null
        } ?: return null

        val fieldContent = when {
            field.getAnnotation(DataTracerFieldEnum::class.java) != null -> {
                val dataTracerFieldEnum = field.getAnnotation(DataTracerFieldEnum::class.java)
                val enumClass = dataTracerFieldEnum?.enumClass
                when (fieldValue) {
                    is Collection<*> -> {
                        val enumDescByValueList = SmartEnumUtil.getEnumDescByValueList(fieldValue, enumClass)
                        enumDescByValueList
                    }

                    else -> {
                        val enumDescByValue = SmartEnumUtil.getEnumDescByValue(fieldValue, enumClass)
                        enumDescByValue
                    }
                }
            }

            field.getAnnotation(DataTracerFieldDict::class.java) != null -> {
                dictCacheService.selectValueNameByValueCodeSplit(fieldValue.toString())
            }

//            field.hasAnnotation<DataTracerFieldSql>() -> {
//                // getRelateDisplayValue(fieldValue, field.getAnnotation(DataTracerFieldSql::class.java))
//            }

            fieldValue is Date -> DateUtil.formatDateTime(fieldValue)
            fieldValue is LocalDateTime -> LocalDateTimeUtil.formatNormal(fieldValue)
            fieldValue is LocalDate -> LocalDateTimeUtil.formatNormal(fieldValue)
            fieldValue is BigDecimal -> {
                field.getAnnotation(DataTracerFieldBigDecimal::class.java)?.let { annotation ->
                    SmartBigDecimalUtil.setScale(fieldValue, annotation.scale).toString()
                } ?: objectMapper.writeValueAsString(fieldValue)
            }

            else -> objectMapper.writeValueAsString(fieldValue)
        }

        return DataTracerContentBO().apply {
            this.field = field
            this.fieldValue = fieldValue
            this.fieldContent = fieldContent
        }
    }

    /**
     * 获取关联字段的显示值
     */
    // private fun getRelateDisplayValue(fieldValue: Any, dataTracerFieldSql: DataTracerFieldSql): String {
    //     val relateMapper = applicationContext.getBean(dataTracerFieldSql.relateMapper.java) as? BaseMapper<*>
    //         ?: return ""
    //
    //     val relateFieldValue = fieldValue.toString()
    //     val qw = QueryWrapper<Any>().apply {
    //         select(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, dataTracerFieldSql.relateDisplayColumn))
    //         `in`(
    //             CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, dataTracerFieldSql.relateColumn),
    //             relateFieldValue
    //         )
    //     }
    //
    //     val displayValue = relateMapper.selectObjs(qw)
    //     return if (displayValue.isEmpty()) "" else SmartStringUtil.join(",", displayValue)
    // }

    /**
     * 获取字段描述信息
     */
//    private fun getFieldDesc(field: Field): String? {
//        val fieldName = field.toGenericString()
//        return fieldDescCacheMap.getOrPut(fieldName) {
//            field.getAnnotation(DataTracerFieldLabel::class.java)?.value ?: ""
//        }.takeIf { it.isNotEmpty() }
//    }

    /**
     * 获取字段描述信息
     */
    private fun getFieldDescK(field: ImmutableProp): String? {
        val fieldName = field.name
        return fieldDescCacheMap.getOrPut(fieldName) {
            field.getAnnotation(DataTracerFieldLabel::class.java)?.value ?: ""
        }.takeIf { it.isNotEmpty() }
    }

    /**
     * 获取操作类型
     */
//    private fun getOperateType(oldObject: Any?, newObject: Any?): String = when {
//        oldObject == null && newObject != null -> DataTracerConst.INSERT
//        oldObject != null && newObject == null -> DataTracerConst.DELETE
//        else -> DataTracerConst.UPDATE
//    }

    /**
     * 校验是否进行比对
     */
//    private fun valid(oldObject: Any?, newObject: Any?): Boolean = when {
//        oldObject == null && newObject == null -> false
//        oldObject == null || newObject == null -> true
//        else -> oldObject::class.java.name == newObject::class.java.name
//    }

    /**
     * 校验列表对象
     */
//    private fun <T> valid(oldObjectList: List<T>, newObjectList: List<T>): Boolean = when {
//        oldObjectList.isEmpty() && newObjectList.isEmpty() -> false
//        oldObjectList.isEmpty() || newObjectList.isEmpty() -> true
//        else -> oldObjectList.firstOrNull()?.let { oldObject ->
//            newObjectList.firstOrNull()?.let { newObject ->
//                oldObject::class.java.name == newObject::class.java.name
//            }
//        } ?: true
//    }

    /**
     * 查询包含file key注解的字段
     */
//    private fun getField(obj: Any): List<Field> {
//        return fieldMap.getOrPut(obj::class.java) {
//            generateSequence(obj::class.java) { it.superclass }
//                .takeWhile { true }
//                .flatMap { clazz ->
//                    clazz.declaredFields
//                        .filter { it.isAnnotationPresent(DataTracerFieldLabel::class.java) }
//                        .onEach { it.isAccessible = true }
//                }
//                .toList()
//        }
//    }

    private fun getKField(obj: Any): List<ImmutableProp> {
        val kClass: KClass<out Any> = obj::class
        val toList = ImmutableType.get(kClass.java).props
            .values
            .filter { it.getAnnotation(DataTracerFieldLabel::class.java) != null }
            .map { it }
            .toList()
        return toList
    }
}

