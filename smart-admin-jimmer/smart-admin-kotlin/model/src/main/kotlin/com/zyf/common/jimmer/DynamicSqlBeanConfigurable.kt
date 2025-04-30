package com.zyf.common.jimmer

import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.employee.Employee
import com.zyf.employee.employeeId
import com.zyf.employee.loginPwd
import com.zyf.helpDoc.HelpDocRelation
import com.zyf.helpDoc.helpDocId
import org.babyfish.jimmer.View
import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.sql.ast.LikeMode
import org.babyfish.jimmer.sql.ast.query.Order
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.KExecutable
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.ast.mutation.KMutableDelete
import org.babyfish.jimmer.sql.kt.ast.mutation.KMutableUpdate
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery
import org.babyfish.jimmer.sql.kt.exists
import java.io.Serializable
import kotlin.reflect.KClass


//fun main() {
//
//    val inputSorts = mutableSetOf<InputSort>()
//    val andConditions = mutableSetOf<InputForm>()
//    val orConditions = mutableSetOf<InputForm>()
//    val outVos = OutVos()
//
//    orConditions.add("enterpriseName" like "1024")
//    orConditions.add(InputForm(InputOperator.EQ, "enterpriseName", "1024创新实验室"))
//    andConditions.add(Enterprise::provinceName eq "河南省")
//    andConditions.add(Enterprise::create dot Employee::actualName like "管理")
//    andConditions.add(
//        InputForm(
//            InputOperator.LIKE,
//            Enterprise::create
//                .dot(Employee::enterprise)
//                .dot(Enterprise::enterpriseName),
//            "1024"
//        )
//    )
//    andConditions.add(InputForm(InputOperator.LIKE, "create.enterprise.create.actualName", "管理"))
//    andConditions.add(
//        InputForm(
//            InputOperator.LIKE,
//            Enterprise::create dot
//                    Employee::enterprise dot
//                    Enterprise::create dot
//                    Employee::enterprise dot
//                    Enterprise::enterpriseName,
//            "1024"
//        )
//    )
//
//    // 排序控制
//    inputSorts.add(InputSort("enterpriseId", InputSort.Desc))
//    inputSorts.add(InputSort(Enterprise::create dot Employee::actualName, InputSort.Asc))
//    inputSorts.add(Enterprise::province.by(InputSort.Asc))
//    inputSorts.add(Enterprise::city by InputSort.Desc)
//
//    // vo形状控制
//    outVos.add(
//        Enterprise::enterpriseName,
//        "provinceName",
//        Enterprise::create dot Employee::actualName,
//        "create.enterprise.enterpriseName",
//        "create.enterprise.create.actualName",
//        Enterprise::create dot
//                Employee::enterprise dot
//                Enterprise::create dot
//                Employee::enterprise dot
//                Enterprise::enterpriseName
//    )
//
//    val exists = exists(
//        sql,
//        Enterprise::class,
//        andConditions,
//        orConditions
//    )
//
//    if (exists) {
//        val list = KSqlClient.queryPage(
//            sql,
//            Enterprise::class,
//            outVos,
//            pageBean,
//            andConditions,
//            orConditions,
////            EnterpriseDetailVO::class
//            inputSorts  // 或者用 EnterpriseDetailVO::class 代替
//        )
//
//
//        println(objectMapper.writeValueAsString(list))
//    }
//}

// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================


class FetchTemp(
    var parentProps: Map<String, ImmutableProp>,
    var fetchImpl: FetcherImplementor<*>
) {
    var nextFetchTemp: FetchTemp? = null
    var nextFieldName: String? = null
}


// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

fun <E : Any, V : View<E>> KSqlClient.list(
    entityClass: KClass<E>,
    view: KClass<V>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): List<V> {
    val kConfigurableRootQuery = this.query(entityClass, view, and, or, sorts)
    return kConfigurableRootQuery.execute()
}

fun <E : Any, V : View<E>> KSqlClient.list(
    entityClass: KClass<E>,
    view: KClass<V>,
    block: (KMutableRootQuery<E>.() -> Unit)?
): List<V> {
    return this.createQuery(entityClass) {
        block?.invoke(this)
        select(table.fetch(view))
    }.execute()
}

//fun <E : Any, V : View<E>> KSqlClient.queryList2(
//    entityClass: KClass<E>,
//    view: KClass<V>,
//    block: (
//            (InputForm?) -> Unit,
//            (InputForm?) -> Unit,
//            (Sort?) -> Unit
//    ) -> Unit,
//): List<V> {
//
//    val and = And()
//    val or = Or()
//    val sorts = Sorts()
//
//    fun addAnd(inputForm: InputForm?) {
//        and.add(inputForm)
//    }
//
//    fun addOr(inputForm: InputForm?) {
//        or.add(inputForm)
//    }
//
//    fun addSort(sort: Sort?) {
//        sorts.add(sort)
//    }
//
//    block(::addAnd, ::addOr, ::addSort)
//
//    val kConfigurableRootQuery = this.query(entityClass, view, and, or, sorts)
//    return kConfigurableRootQuery.execute()
//}

fun <E : Any> KSqlClient.list(
    entityClass: KClass<E>,
    outVos: OutVos,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): List<E> = this.list(entityClass, outVos.toMutableSet(), and, or, sorts)


fun <E : Any> KSqlClient.list(
    entityClass: KClass<E>,
    outVos: MutableSet<OutVo>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): List<E> {
    val kConfigurableRootQuery = this.query(entityClass, outVos, and, or, sorts)
    return kConfigurableRootQuery.execute()
}


// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================


fun <E : Any, V : View<E>> KSqlClient.page(
    entityClass: KClass<E>,
    view: KClass<V>,
    pageBean: PageBean,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): PageResult<V> {
    val kConfigurableRootQuery = this.query(entityClass, view, and, or, sorts)
    return kConfigurableRootQuery.page(pageBean)
}

fun <E : Any> KSqlClient.page(
    entityClass: KClass<E>,
    outVos: OutVos,
    pageBean: PageBean,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): PageResult<E> = this.page(entityClass, outVos.toMutableSet(), pageBean, and, or, sorts)

fun <E : Any> KSqlClient.page(
    entityClass: KClass<E>,
    outVos: MutableSet<OutVo>,
    pageBean: PageBean,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): PageResult<E> {
    val kConfigurableRootQuery = this.query(entityClass, outVos, and, or, sorts)
    return kConfigurableRootQuery.page(pageBean)
}

fun <E : Any> KSqlClient.page(
    entityClass: KClass<E>,
    pageBean: PageBean,
    block: KMutableRootQuery<E>.() -> Unit
): PageResult<E> {
    return this.createQuery(entityClass) {
        this.block()
        select(table)
    }.page(pageBean)
}

fun <E : Any, V : View<E>> KSqlClient.page(
    entityClass: KClass<E>,
    view: KClass<V>,
    pageBean: PageBean,
    block: KMutableRootQuery<E>.() -> Unit
): PageResult<V> {
    return this.createQuery(entityClass) {
        this.block()
        select(table.fetch(view))
    }.page(pageBean)
}

// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

fun <E : Any, V : View<E>> KSqlClient.queryOne(
    entityClass: KClass<E>,
    view: KClass<V>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): V {
    val kConfigurableRootQuery = this.query(entityClass, view, and, or, sorts)
    return kConfigurableRootQuery.fetchOne()
}

fun <E : Any> KSqlClient.queryOne(
    entityClass: KClass<E>,
    outVos: OutVos,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): E = this.queryOne(entityClass, outVos.toMutableSet(), and, or, sorts)

fun <E : Any> KSqlClient.queryOne(
    entityClass: KClass<E>,
    outVos: MutableSet<OutVo>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): E {
    val kConfigurableRootQuery = this.query(entityClass, outVos, and, or, sorts)
    return kConfigurableRootQuery.fetchOne()
}

// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

fun <E : Any, T : Serializable> KSqlClient.notExistsById(entityClass: KClass<E>, id: T): Boolean {
    return this.exists(entityClass) {
        where(table.get<T>(ImmutableType.get(entityClass.java).idProp) ne id)
    }
}

fun <E: Any, T : Serializable> KSqlClient.byId(
    entityClass: KClass<E>,
    id: T
): E? = this.findById(entityClass, id)

fun <E : Any, T : Serializable> KSqlClient.byId(
    entityClass: KClass<E>,
    id: T,
    block: (() -> org.babyfish.jimmer.sql.fetcher.Fetcher<E>)? = null
): E? = block?.let {
    return this.createQuery(entityClass) {
        where(table.get<T>(ImmutableType.get(entityClass.java).idProp) eq id)
        select(table.fetch(block()))
    }.fetchOneOrNull()
} ?: this.findById(entityClass, id)

// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

fun <E : Any> KSqlClient.simpleUpdate(
    entityType: KClass<E>,
    block: KMutableUpdate<E>.() -> Unit
): Int {
    return this.createUpdate(entityType) {
        block()
    }.execute()
}

fun <E : Any> KSqlClient.delete(
    entityType: KClass<E>,
    block: KMutableDelete<E>.() -> Unit
): Int {
    return this.createDelete(entityType) {
        block()
    }.execute()
}


// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

fun <E : Any, V : View<E>> KSqlClient.oneOrNull(
    entityClass: KClass<E>,
    view: KClass<V>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): V? {
    val kConfigurableRootQuery = this.query(entityClass, view, and, or, sorts)
    return kConfigurableRootQuery.fetchOneOrNull()
}

fun <E : Any> KSqlClient.oneOrNull(
    entityClass: KClass<E>,
    outVos: OutVos,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): E? = this.oneOrNull(entityClass, outVos.toMutableSet(), and, or, sorts)

fun <E : Any> KSqlClient.oneOrNull(
    entityClass: KClass<E>,
    outVos: MutableSet<OutVo>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): E? {
    val kConfigurableRootQuery = this.query(entityClass, outVos, and, or, sorts)
    return kConfigurableRootQuery.fetchOneOrNull()
}

fun <E : Any> KSqlClient.oneOrNull(
    entityClass: KClass<E>,
    block: KMutableRootQuery<E>.() -> Unit
): E? {
    return this.createQuery(entityClass) {
        this.block()
        select(table)
    }.fetchOneOrNull()
}

fun <E : Any, V : View<E>> KSqlClient.oneOrNull(
    entityClass: KClass<E>,
    view: KClass<V>,
    block: KMutableRootQuery<E>.() -> Unit
): V? {
    return this.createQuery(entityClass) {
        this.block()
        select(table.fetch(view))
    }.fetchOneOrNull()
}

// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

//fun <E : Any> KSqlClient.exists(
//    entityClass: KClass<E>,
//    block: KMutableRootQuery<E>.() -> Unit
//): Boolean {
//    return this.exists(entityClass) {
//        this.block()
//    }
//}

fun <E : Any> KSqlClient.exists(
    entityClass: KClass<E>,
    and: And = And(),
    or: Or = Or(),
): Boolean {
    return this.exists(entityClass) {
        val andConditions = and.toMutableSet()
        if (andConditions.isNotEmpty()) {
            where(buildAndMultiCondition(andConditions))
        }
        val orConditions = or.toMutableSet()
        if (orConditions.isNotEmpty()) {
            where(buildOrMultiCondition(orConditions))
        }
    }
}

fun <E : Any> KSqlClient.notExists(
    type: KClass<E>,
    block: KMutableRootQuery<E>.() -> Unit = {}
): Boolean = queries.forEntity(type) {
    block()
    select(constant(1))
}.execute().isEmpty()


// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================


fun <E : Any> KSqlClient.unlimitedCount(
    type: KClass<E>,
    block: KMutableRootQuery<E>.() -> Unit = {}
): Long = queries.forEntity(type) {
    block()
    select(count(table))
}.fetchUnlimitedCount()


// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================


fun <E : Any, V : View<E>> KSqlClient.query(
    entityClass: KClass<E>,
    view: KClass<V>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): KConfigurableRootQuery<E, V> {
    val kConfigurableRootQuery = this.createQuery(entityClass) {
        val andConditions = and.toMutableSet()
        if (andConditions.isNotEmpty()) {
            where(buildAndMultiCondition(andConditions))
        }
        val orConditions = or.toMutableSet()
        if (orConditions.isNotEmpty()) {
            where(buildOrMultiCondition(orConditions))
        }
        val inputSortList = sorts.toMutableSet()
        if (inputSortList.isNotEmpty()) {
            buildOrder(inputSortList)
        }
        select(table.fetch(view))
    }
    return kConfigurableRootQuery
}


fun <E : Any> KSqlClient.query(
    entityClass: KClass<E>,
    outVos: MutableSet<OutVo>,
    and: And = And(),
    or: Or = Or(),
    sorts: Sorts = Sorts(),
): KConfigurableRootQuery<E, E> {
    var fetcherImplementor = FetcherImpl(entityClass.java) as FetcherImplementor<E>
    val mainImmutableType = fetcherImplementor.immutableType
    fetcherImplementor = fetcherImplementor.add(mainImmutableType.idProp.name)

    outVos.forEach { vo ->

        if (vo.columnKey.isBlank()) {
            return@forEach
        }

        val columnKey = vo.columnKey.trim()

        if (columnKey.contains(".")) {
            val split = columnKey.split(".")

            var props = mainImmutableType.props
            val fetchTemps: MutableList<FetchTemp> = mutableListOf()

            var parentFetchTemp: FetchTemp? = null
            split.dropLast(1).forEach { fieldName ->
                props[fieldName]?.let { immutableProp ->
                    var tempFetcher = FetcherImpl(immutableProp.elementClass) as FetcherImplementor<*>
                    val tempImmutableType = tempFetcher.immutableType
                    tempFetcher = tempFetcher.add(tempImmutableType.idProp!!.name)
                    val fetchTemp = FetchTemp(props, tempFetcher)
                    parentFetchTemp?.nextFetchTemp = fetchTemp
                    parentFetchTemp?.nextFieldName = fieldName

                    fetchTemps.add(fetchTemp)

                    parentFetchTemp = fetchTemp
                    props = tempImmutableType.props
                }
            }

            fetchTemps.reverse()

            var lastjoinInfo: String? = null
            var createFetcher: FetcherImplementor<*>? = null

            val splitReversed = split.asReversed()
            for ((index, it) in splitReversed.withIndex()) {
                if (lastjoinInfo == null) {
                    lastjoinInfo = it
                } else {
                    val child = fetchTemps[index - 1]
                    val fetchImpl = child.fetchImpl
                    val parentProps = child.parentProps
                    val nextFetchTemp = child.nextFetchTemp
                    val nextFieldName = child.nextFieldName

                    if (createFetcher == null) {
                        parentProps[it]?.let {
                            createFetcher = fetchImpl.add(lastjoinInfo) as FetcherImplementor<*>
                            child.fetchImpl = createFetcher!!
                        }
                    } else {
                        parentProps[it]?.let {
                            createFetcher = fetchImpl.add(nextFieldName, nextFetchTemp!!.fetchImpl)
                            child.fetchImpl = createFetcher!!
                        }
                    }
                }
            }

            val firstJoinTableName = split.first()
            fetcherImplementor = fetcherImplementor.add(firstJoinTableName, createFetcher)

        } else {
            fetcherImplementor = fetcherImplementor.add(
                columnKey
            )
        }
    }

    val kConfigurableRootQuery = this.createQuery(entityClass) {
        val andConditions = and.toMutableSet()
        if (andConditions.isNotEmpty()) {
            where(buildAndMultiCondition(andConditions))
        }
        val orConditions = or.toMutableSet()
        if (orConditions.isNotEmpty()) {
            where(buildOrMultiCondition(orConditions))
        }
        val inputSortList = sorts.toMutableSet()
        if (inputSortList.isNotEmpty()) {
            buildOrder(inputSortList)
        }
        val selection = table.fetch(fetcherImplementor)
        select(selection)
    }
    return kConfigurableRootQuery
}

// =======================================================================================================================
// =======================================================================================================================
// =======================================================================================================================

fun <E : Any> KMutableRootQuery<E>.buildOrder(
    sorts: Collection<Sort>
): Unit {
    return orderBy(*buildOrderByArray(sorts))
}

fun <E : Any> KMutableRootQuery<E>.buildOrMultiCondition(
    stateSearchConditions: Collection<InputForm>
): KNonNullExpression<Boolean>? {
    return or(*buildExpressionsArray(stateSearchConditions))
}

fun <E : Any> KMutableRootQuery<E>.buildAndMultiCondition(
    stateSearchConditions: Collection<InputForm>
): KNonNullExpression<Boolean>? {
    return and(*buildExpressionsArray(stateSearchConditions))
}

fun <E : Any> KMutableRootQuery<E>.buildExpressionsArray(
    stateSearchConditions: Collection<InputForm>
): Array<KNonNullExpression<Boolean>?> {
    return stateSearchConditions.map {
        buildConditions(it.operator, it.columnKey.trim(), it.value)
    }.toTypedArray()
}


fun <E : Any> KMutableRootQuery<E>.buildOrderByArray(
    sorts: Collection<Sort>
): Array<Order> {
    return sorts.map {
        buildOrderBys(it)
    }.toTypedArray()
}

fun <E : Any> KMutableRootQuery<E>.buildOrderBys(
    sort: Sort
): Order {
    var columnKey = sort.columnKey.trim()
    var finalTable = table

    if (columnKey.contains(".")) {
        val split = columnKey.split(".")
        val joinColumnKey = split.last()

        split.dropLast(1).forEach {
            finalTable = finalTable.join<E>(it)
        }
        columnKey = joinColumnKey
    }

    if (sort.order.trim() == "desc") {
        return finalTable.get<String>(columnKey).desc()
    }
    return finalTable.get<String>(columnKey).asc()
}


fun <E : Any> KMutableRootQuery<E>.buildConditions(
    operator: InputOperator, columnName: String, value: Any?
): KNonNullExpression<Boolean> {
    // join(EnterpriseProps.CREATE.unwrap())
    var columnKey = columnName
    var finalTable = table
    if (columnKey.contains(".")) {
        val split = columnKey.split(".")
        val joinColumnKey = split.last()

        split.dropLast(1).forEach {
            finalTable = finalTable.join<E>(it)
        }
        columnKey = joinColumnKey
    }

    val predicate = when (operator) {
        InputOperator.EQ -> finalTable.get<Any>(columnKey).eq(value)
        InputOperator.NE -> finalTable.get<Any>(columnKey).ne(value)
        InputOperator.LIKE -> finalTable.get<String>(columnKey).ilike(value as String, LikeMode.ANYWHERE)
        InputOperator.GT -> finalTable.get<Comparable<Any>>(columnKey).gt(value as Comparable<Any>)
        InputOperator.GE -> finalTable.get<Comparable<Any>>(columnKey).ge(value as Comparable<Any>)
        InputOperator.LT -> finalTable.get<Comparable<Any>>(columnKey).lt(value as Comparable<Any>)
        InputOperator.LE -> finalTable.get<Comparable<Any>>(columnKey).le(value as Comparable<Any>)
        InputOperator.IN -> finalTable.get<Any>(columnKey).valueIn(value as Collection<Any>)
        InputOperator.NOT_IN -> finalTable.get<Any>(columnKey).valueNotIn(value as Collection<Any>)
        InputOperator.BETWEEN -> {
            val (start, end) = value as Pair<*, *>
            finalTable.get<Comparable<Any>>(columnKey).between(
                start as Comparable<Any>,
                end as Comparable<Any>
            )
        }

        InputOperator.NOT_BETWEEN -> {
            val (start, end) = value as Pair<*, *>
            finalTable.get<Comparable<Any>>(columnKey).notBetween(
                start as Comparable<Any>,
                end as Comparable<Any>
            )
        }

        InputOperator.IS_NULL -> finalTable.get<Any>(columnKey).isNull()
        InputOperator.IS_NOT_NULL -> finalTable.get<Any>(columnKey).isNotNull()

        InputOperator.STARTS_WITH -> finalTable.get<String>(columnKey).like(value as String, LikeMode.START)
        InputOperator.ENDS_WITH -> finalTable.get<String>(columnKey).like(value as String, LikeMode.END)
    }
    return predicate
}