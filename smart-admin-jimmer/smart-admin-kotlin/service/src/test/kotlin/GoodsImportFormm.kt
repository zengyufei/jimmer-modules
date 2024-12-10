import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonProperty
import com.zyf.common.convert.ExcelEnum
import com.zyf.common.enums.GoodsStatusEnum
import java.math.BigDecimal

open class GoodsImportFormm {

    @JsonProperty(
        "goodsName",
        required = true,
    )
    @field:ExcelProperty(("商品名称"))
    var goodsName: String? = null

    @JsonProperty("goodsStatus")
    @field:ExcelProperty(
        `value` = [
            "商品状态"
        ],
        converter = ExcelEnum::class
    )
    var goodsStatus: GoodsStatusEnum? = null

    @JsonProperty(
        "price",
        required = true,
    )
    @field:ExcelProperty(("商品价格"))
    var price: BigDecimal? = null
}