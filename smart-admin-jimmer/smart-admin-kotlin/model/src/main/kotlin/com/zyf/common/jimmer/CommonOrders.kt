package com.zyf.common.jimmer

import com.zyf.common.domain.PageBean
import org.babyfish.jimmer.sql.ast.query.Order
import org.babyfish.jimmer.sql.ast.table.Props

object CommonOrders {

    private val EMPTY_ORDERS = listOf<Order?>()

    fun toOrders(table: Props?, pageBean: PageBean?): List<Order?> {
        if (pageBean == null || pageBean.sortCode.isNullOrBlank()) {
            return EMPTY_ORDERS
        }
        return  Order.makeOrders(table, pageBean.sortCode)
    }
}
