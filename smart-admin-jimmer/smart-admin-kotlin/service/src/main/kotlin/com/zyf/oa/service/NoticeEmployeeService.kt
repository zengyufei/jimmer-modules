package com.zyf.oa.service

import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.NoticeVisibleRangeDataTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.department.service.DepartmentService
import com.zyf.employee.Employee
import com.zyf.employee.departmentId
import com.zyf.employee.employeeId
import com.zyf.oa.*
import com.zyf.oa.domain.NoticeVisibleRangeVO
import com.zyf.service.dto.*
import com.zyf.support.service.DataTracerService
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class NoticeEmployeeService(
    val sql: KSqlClient,
    val noticeService: NoticeService,
    val noticeTypeService: NoticeTypeService,
    val dataTracerService: DataTracerService,
    val departmentService: DepartmentService,
) {

    /**
     * 查询我的 通知、公告清单
     */
    fun queryList(
        requestEmployeeId: String,
        pageBean: PageBean,
        noticeEmployeeQueryForm: NoticeEmployeeQueryForm
    ): ResponseDTO<PageResult<NoticeEmployeeVO>> {

        val employee: Employee? = sql.findById(Employee::class, requestEmployeeId)
        if (employee == null) {
            throw IllegalArgumentException("用户不存在")
        }
        // 如果不是管理员 则获取请求人的 部门及其子部门
        var employeeDepartmentIdList: List<String>? = mutableListOf()
        val departmentId = employee.departmentId
        if (!employee.administratorFlag && departmentId != null) {
            employeeDepartmentIdList = departmentService.selfAndChildrenIdList(departmentId)
        }

        val noticeList = sql.createQuery(Notice::class) {
            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.publishTime.desc())
            where(noticeEmployeeQueryForm)
            if (noticeEmployeeQueryForm.notViewFlag == true) {
                where(table.allVisibleFlag eq true)
            }
            where(table.publishTime lt LocalDateTime.now())

            // 不是管理员
            if (!employee.administratorFlag) {
                if (noticeEmployeeQueryForm.notViewFlag == true) {
                    where(
                        or(
                            table.employeeRanges {
                                this.employeeId eq requestEmployeeId
                            },
                            table.departmentRanges {
                                this.departmentId `valueIn?` employeeDepartmentIdList
                            }
                        )
                    )
                } else {
                    where(
                        or(
                            table.employeeRanges {
                                this.employeeId eq requestEmployeeId
                            },
                            table.departmentRanges {
                                this.departmentId `valueIn?` employeeDepartmentIdList
                            },
                            table.allVisibleFlag eq true // 区别2
                        )
                    )
                }
            }

            val noticeId = table.noticeId
            val countSubQuery = subQuery(NoticeViewRecord::class) {
                where(table.employeeId eq requestEmployeeId)
                where(table.noticeId eq noticeId)
                select(count(table))
            }
            if (noticeEmployeeQueryForm.notViewFlag == true) {
                where(countSubQuery eq 0)
            }
            select(
                table.fetch(NoticeEmployeeVO::class),
                countSubQuery
            )
        }.page(pageBean) {
            val noticeEmployeeVO = it._1
            val viewFlag = it._2
            NoticeEmployeeVO(noticeEmployeeVO.toEntity()).copy(
                // 设置发布日期
                publishDate = noticeEmployeeVO.publishTime.toLocalDate(),
                viewFlag = viewFlag.toInt()
            )
        }

        return ResponseDTO.ok(noticeList)
    }


    /**
     * 查询我的 待查看的 通知、公告清单
     */
    fun view(requestEmployeeId: String, noticeId: String, ip: String?, userAgent: String?): ResponseDTO<NoticeDetailVO?> {
        val updateFormVO: NoticeUpdateFormVO? = noticeService.getUpdateFormVO(noticeId)
        if (updateFormVO == null) {
            return ResponseDTO.userErrorParam("通知公告不存在")
        }

        val employee: Employee? = sql.findById(Employee::class, requestEmployeeId)
        employee?.let {
            if (!employee.administratorFlag) {
                it.departmentId?.let { departmentId ->
                    if (!updateFormVO.allVisibleFlag && !checkVisibleRange(updateFormVO.visibleRangeList, requestEmployeeId, departmentId)) {
                        return ResponseDTO.userErrorParam("对不起，您没有权限查看内容")
                    }
                }
            }
        }


        var noticeDetailVO = NoticeDetailVO(updateFormVO.toEntity())


        val viewCount: Long = sql.createQuery(NoticeViewRecord::class) {
            where(table.noticeId eq noticeId)
            where(table.employeeId eq requestEmployeeId)
            select(count(table))
        }.fetchUnlimitedCount()
        if (viewCount == 0L) {
            sql.insert(NoticeViewRecord {
                this.noticeId = noticeId
                this.employeeId = requestEmployeeId
                this.firstIp = ip
                this.firstUserAgent = userAgent
                this.pageViewCount = 1
            })
            // 该员工对于这个通知是第一次查看 页面浏览量+1 用户浏览量+1
            sql.createUpdate(Notice::class) {
                set(table.pageViewCount, table.pageViewCount + 1)
                set(table.userViewCount, table.userViewCount + 1)
                where(table.noticeId eq noticeId)
            }.execute()

            noticeDetailVO = noticeDetailVO.copy(
                pageViewCount = noticeDetailVO.pageViewCount + 1,
                userViewCount = noticeDetailVO.userViewCount + 1
            )
        } else {
            sql.createUpdate(NoticeViewRecord::class) {
                set(table.pageViewCount, table.pageViewCount + 1)
                set(table.lastIp, ip)
                set(table.lastUserAgent, userAgent)
                where(table.noticeId eq noticeId)
                where(table.employeeId eq requestEmployeeId)
            }.execute()
            // 该员工对于这个通知不是第一次查看 页面浏览量+1 用户浏览量+0
            sql.createUpdate(Notice::class) {
                set(table.pageViewCount, table.pageViewCount + 1)
                where(table.noticeId eq noticeId)
            }.execute()
            noticeDetailVO = noticeDetailVO.copy(
                pageViewCount = noticeDetailVO.pageViewCount + 1,
            )
        }

        return ResponseDTO.ok(noticeDetailVO)
    }

    /**
     * 校验是否有查看权限的范围
     *
     */
    fun checkVisibleRange(visibleRangeList: List<NoticeVisibleRangeVO>, employeeId: String, departmentId: String): Boolean {
        // 员工范围
        val anyMatch = visibleRangeList
            .filter { NoticeVisibleRangeDataTypeEnum.EMPLOYEE.equalsValue(it.dataType!!) }
            .filter { it.dataId == employeeId }
            .any()
        if (anyMatch) {
            return true
        }

        // 部门范围
        val visibleDepartmentIdList = visibleRangeList
            .filter { NoticeVisibleRangeDataTypeEnum.DEPARTMENT.equalsValue(it.dataType!!) }
            .map { it.dataId!! }

        for (visibleDepartmentId in visibleDepartmentIdList) {
            val departmentIdList: List<String> = departmentService.selfAndChildrenIdList(visibleDepartmentId)
            if (departmentIdList.contains(departmentId)) {
                return true
            }
        }
        return false
    }

    /**
     * 分页查询  查看记录
     */
    fun queryViewRecord(pageBean: PageBean, noticeViewRecordQueryForm: NoticeViewRecordQueryForm?): PageResult<NoticeViewRecordVO> {
        val pageResult = sql.createQuery(NoticeViewRecord::class) {
            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.updateTime.desc(), table.createTime.desc())
            where(noticeViewRecordQueryForm)
            select(table.fetch(NoticeViewRecordVO::class))
        }.page(pageBean)
        return pageResult
    }

}