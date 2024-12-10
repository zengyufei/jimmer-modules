package com.zyf.goods

import java.math.BigDecimal
import java.time.LocalDateTime
import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import com.zyf.common.enums.GoodsStatusEnum
import org.babyfish.jimmer.sql.*


/**
 * <p>
 *  商品

 * </p>
 *
 * @author zyf
 * @date 2024-12-05
 */
@Entity
@Table(name = "t_goods")
interface Goods : TenantAware, BaseEntity {

    /**
     *  goods_id

     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "goods_id")
    val goodsId: String

    /**
     *  商品状态:[1:预约中,2:售卖中,3:售罄]
     */
    @Column(name = "goods_status")
    val goodsStatus: GoodsStatusEnum?

    /**
     *  商品类目
     */
    @IdView("category")
    val categoryId: String?

    /**
     *  商品名称
     */
    @Column(name = "goods_name")
    val goodsName: String

    /**
     *  产地
     */
    @Column(name = "place")
    val place: String?

    /**
     *  价格
     */
    @Column(name = "price")
    val price: BigDecimal

    /**
     *  上架状态
     */
    @Column(name = "shelves_flag")
    val shelvesFlag: Boolean

    /**
     *  备注
     */
    @Column(name = "remark")
    val remark: String?

    @ManyToOne(inputNotNull = true)
    @JoinColumn(name="category_id")
    val category: Category?

}
