package com.zyf.goods

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*


/**
 * <p>
 *  分类表，主要用于商品分类

 * </p>
 *
 * @author zyf
 * @date 2024-12-05
 */
@Entity
@Table(name = "t_category")
interface Category : TenantAware, BaseEntity {

    /**
     *  分类id
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "category_id")
    val categoryId: String

    /**
     *  分类名称
     */
    @Column(name = "category_name")
    val categoryName: String

    /**
     *  分类类型
     */
    @Column(name = "category_type")
    val categoryType: Int

    /**
     *  父级id
     */
    @IdView("parent")
    val parentId: String?

    /**
     *  排序
     */
    @Column(name = "sort")
    val sort: Int

    /**
     *  是否禁用
     */
    @Column(name = "disabled_flag")
    val disabledFlag: Boolean

    /**
     *  remark
     */
    @Column(name = "remark")
    val remark: String?

    @IdView("children")
    val childrenIds: List<String>

    @IdView("goodes")
    val goodsIds: List<String>

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: Category?

    @OneToMany(mappedBy = "parent")
    val children: List<Category>


    @OneToMany(mappedBy = "category")
    val goodes: List<Goods>
}
