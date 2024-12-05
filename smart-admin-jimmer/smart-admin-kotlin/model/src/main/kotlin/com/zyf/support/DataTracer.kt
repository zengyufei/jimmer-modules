package com.zyf.support

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

/**
 * 数据记录 实体
 */
@Entity
@Table(name = "t_data_tracer")
interface DataTracer : TenantAware, BaseEntity {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "data_tracer_id")
    val dataTracerId: String
    
    /**
     * 数据id
     */
    @Column(name = "data_id")
    val dataId: String
    
    /**
     * 业务类型
     */
    @Column(name = "type")
    val type: Int
    
    /**
     * 内容
     */
    @Column(name = "content")
    val content: String?
    
    /**
     * diff 差异：旧的数据
     */
    @Column(name = "diff_old")
    val diffOld: String?
    
    /**
     * 差异：新的数据
     */
    @Column(name = "diff_new")
    val diffNew: String?
    
    /**
     * 扩展字段
     */
    @Column(name = "extra_data")
    val extraData: String?
    
    /**
     * 用户
     */
    @Column(name = "user_id")
    val userId: String
    
    /**
     * 用户类型
     */
    @Column(name = "user_type")
    val userType: Int
    
    /**
     * 用户名
     */
    @Column(name = "user_name")
    val userName: String
    
    /**
     * 请求ip
     */
    @Column(name = "ip")
    val ip: String?
    
    /**
     * 请求ip地区
     */
    @Column(name = "ip_region")
    val ipRegion: String?
    
    /**
     * 请求头
     */
    @Column(name = "user_agent")
    val userAgent: String?
}