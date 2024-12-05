package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_dict_key")
interface DictKey : TenantAware, BaseEntity {

    /** 字段keyid */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "dict_key_id")
    val dictKeyId: String

    /** 编码 */
    @Key
    @Column(name = "key_code")
    val keyCode: String

    /** 名称 */
    @Column(name = "key_name")
    val keyName: String

    /** 备注 */
    @Column(name = "remark")
    val remark: String?

    @OneToMany(mappedBy = "dictKey")
    val dictValues: List<DictValue>
}