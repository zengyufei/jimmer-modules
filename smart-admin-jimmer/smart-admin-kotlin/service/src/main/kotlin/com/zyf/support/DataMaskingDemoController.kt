package com.zyf.support

import cn.hutool.core.util.RandomUtil
import com.zyf.common.annotations.DataMasking
import com.zyf.common.annotations.Operation
import com.zyf.common.annotations.Tag
import com.zyf.common.constant.SwaggerTagConst
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.DataMaskingTypeEnum
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 数据脱敏demo
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2024/08/01 22:07:27
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */

@RestController
@Tag(name = SwaggerTagConst.Support.DATA_MASKING)
class DataMaskingDemoController {

    @Operation(summary = "数据脱敏demo @author 1024创新实验室-主任-卓大")
    @GetMapping("/support/dataMasking/demo/query")
    fun query(): ResponseDTO<List<DataVO>> {
        val list = mutableListOf<DataVO>()
        for (i in 0 until RandomUtil.randomInt(10, 16)) {
            val data = DataVO().apply {
                userId = RandomUtil.randomLong(1328479238, 83274298347982L)
                phone = "1" + RandomUtil.randomNumbers(10)
                idCard = "410" + RandomUtil.randomNumbers(3) +
                          RandomUtil.randomInt(1980, 2010) +
                          RandomUtil.randomInt(10, 12) +
                          RandomUtil.randomInt(10, 30) +
                          RandomUtil.randomNumbers(4)
                address = if (RandomUtil.randomBoolean()) {
                    "河南省洛阳市洛龙区一零二四大街1024号"
                } else {
                    "河南省郑州市高新区六边形大街六边形大楼"
                }
                password = RandomUtil.randomString(10)
                email = "${RandomUtil.randomString(RandomUtil.randomInt(6, 10))}@${RandomUtil.randomString(2)}.com"
                carLicense = "豫${RandomStringUtils.randomAlphabetic(1).uppercase()} ${RandomStringUtils.randomAlphanumeric(5).uppercase()}"
                bankCard = "6225${RandomStringUtils.randomNumeric(14)}"
                other = RandomStringUtils.randomAlphanumeric(1, 12)
            }
            list.add(data)
        }
        return ResponseDTO.ok(list)
    }

    data class DataVO(

        @field:DataMasking(DataMaskingTypeEnum.USER_ID)
        var userId: Long? = null,

        @field:DataMasking(DataMaskingTypeEnum.PHONE)
        var phone: String? = null,

        @field:DataMasking(DataMaskingTypeEnum.ID_CARD)
        var idCard: String? = null,

        @field:DataMasking(DataMaskingTypeEnum.ADDRESS)
        var address: String? = null,

        @field:DataMasking(DataMaskingTypeEnum.PASSWORD)
        var password: String? = null,

        @field:DataMasking(DataMaskingTypeEnum.EMAIL)
        var email: String? = null,

        @field:DataMasking(DataMaskingTypeEnum.CAR_LICENSE)
        var carLicense: String? = null,

        @field:DataMasking(DataMaskingTypeEnum.BANK_CARD)
        var bankCard: String? = null,

        @field:DataMasking
        var other: String? = null
    )
}
