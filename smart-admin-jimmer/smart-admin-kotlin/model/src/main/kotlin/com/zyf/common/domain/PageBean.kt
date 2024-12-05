package com.zyf.common.domain

class PageBean {
    var pageNum: Int = 1
    var pageSize: Int = 10
    var sortCode: String? = null
    var searchCount: Boolean = true

    companion object {
        fun of(pageNum: Int, pageSize: Int): PageBean {
            require(pageNum >= 0) { "pageNum cannot be negative" }
            require(pageSize >= 1) { "pageSize must be positive" }
            val pageBean = PageBean()
            pageBean.pageNum = pageNum
            pageBean.pageSize = pageSize
            return pageBean
        }


        fun of(pageNum: Int, pageSize: Int, sortCode: String?): PageBean {
            require(pageNum >= 0) { "pageNum cannot be negative" }
            require(pageSize >= 1) { "pageSize must be positive" }
            val pageBean = PageBean()
            pageBean.pageNum = pageNum
            pageBean.pageSize = pageSize
            pageBean.sortCode = sortCode
            return pageBean
        }
    }
}
