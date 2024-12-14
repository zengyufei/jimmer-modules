package com.zyf.common.base

import com.fasterxml.jackson.annotation.JsonFormat
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

/*
 * see CommonEntityDraftInterceptor
 */
@MappedSuperclass
interface BaseEntity {

    @IdView("create")
    val createId: String?

    @IdView("update")
    val updateId: String?

    @OneToOne(inputNotNull = true)
    @JoinColumn(name="create_by", foreignKeyType = ForeignKeyType.FAKE)
    val create: Employee?

    @OneToOne
    @JoinColumn(name="update_by", foreignKeyType = ForeignKeyType.FAKE)
    val update: Employee?

    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="create_time")
    val createTime: LocalDateTime

    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="update_time")
    val updateTime: LocalDateTime?

    /** 删除标记，0未删除，1已删除 */
//    @Default("0")
    @LogicalDeleted("1")
    @Column(name="delete_flag")
    val deleteFlag: Int

}