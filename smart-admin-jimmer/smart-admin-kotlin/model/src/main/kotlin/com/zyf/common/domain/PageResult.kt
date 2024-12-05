package com.zyf.common.domain


/**
 * 分页返回对象
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2020/04/28 16:19
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class PageResult<T> {
    /**
     * 当前页
     */
    var pageNum: Long? = null

    /**
     * 每页的数量
     */
    var pageSize: Long? = null

    /**
     * 总记录数
     */
    var total: Long? = null

    /**
     * 总页数
     */
    var pages: Long? = null

    /**
     * 结果集
     */
    var list: List<T>? = null

    /**
     * 是否为空
     */
    var emptyFlag: Boolean = true
}
