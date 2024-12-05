package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.employee.Employee
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.Table

@Entity
@Table(name = "t_file")
interface FileInfo : TenantAware, BaseEntity {

    /** 文件id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "file_id")
    val fileId: String

    /** 文件类型 */
    @Column(name = "folder_type")
    val folderType: Int

    /** 文件名称 */
    @Column(name = "file_name")
    val fileName: String?

    /** 文件大小 */
    @Column(name = "file_size")
    val fileSize: Long?

    /** 文件key，用于文件下载 */
    @Column(name = "file_key")
    val fileKey: String

    /** 文件类型 */
    @Column(name = "file_type")
    val fileType: String

    /** 创建人，即上传人 */
    @IdView("creator")
    val creatorId: String?

    /** 创建人用户类型 */
    @Column(name = "creator_user_type")
    val creatorUserType: Int?

    /** 创建人姓名 */
    @Column(name = "creator_name")
    val creatorName: String?

    @ManyToOne
    @JoinColumn(name = "creator_id")
    val creator: Employee?
}