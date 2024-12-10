import com.alibaba.excel.annotation.ExcelProperty
import com.zyf.common.convert.ExcelEnum
import com.zyf.common.enums.GoodsStatusEnum
import java.math.BigDecimal

open class GoodsExcelVVo constructor(

    @field:ExcelProperty(("商品名称"))
    val goodsName: String,

    @field:ExcelProperty(
        `value` = [
            "商品状态"
        ],
        converter = ExcelEnum::class
    )
    val goodsStatus: GoodsStatusEnum? = null,

    @field:ExcelProperty(("商品价格"))
    val price: BigDecimal,
)