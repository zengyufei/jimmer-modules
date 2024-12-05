package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_dict_value")
interface DictValue : TenantAware, BaseEntity {

    /** 字段keyid */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "dict_value_id")
    val dictValueId: String

    @IdView("dictKey")
    val dictKeyId: String?

    /** 编码 */
    @Column(name = "value_code")
    val valueCode: String

    /** 名称 */
    @Column(name = "value_name")
    val valueName: String

    /** 备注 */
    @Column(name = "remark")
    val remark: String?

    /** 排序 */
    @Column(name = "sort")
    val sort: Int

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name = "dict_key_id")
    val dictKey: DictKey?
}