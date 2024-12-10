import com.alibaba.excel.EasyExcel
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.GoodsStatusEnum
import com.zyf.common.exception.BusinessException
import com.zyf.service.dto.GoodsImportForm
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal

class TestExportEnum {

    @Test
    fun testImport() {
        val objectMapper = ObjectMapper()

        val sheetName = "test"
        val downFilePath = "D:\\Edge浏览器下载"
        val inputStream = FileInputStream("$downFilePath/test.xlsx")

        val dataList = try {
            val doReadSync = EasyExcel.read(inputStream)
                .head(GoodsImportFormm::class.java)
                .sheet(sheetName)
                .doReadSync<Any>()
            doReadSync
        } catch (e: IOException) {
            log.error(e.message, e)
            throw BusinessException("数据格式存在问题，无法读取")
        }

        if (dataList.isEmpty()) {
            println("数据为空")
        } else {
            val msg = "成功导入${dataList.size}条, 数据为：${objectMapper.writeValueAsString(dataList)}"
            println(msg)
        }

    }

    @Test
    fun testExport() {
        val sheetName = "test"
        val downFilePath = "D:\\Edge浏览器下载"
        val outputStream = FileOutputStream("$downFilePath/test.xlsx")

        val data: MutableList<GoodsExcelVVo> = mutableListOf()
        data.add(GoodsExcelVVo(
            goodsName = "商品1",
            goodsStatus = GoodsStatusEnum.APPOINTMENT,
            price = BigDecimal(100)
        ))

        data.add(GoodsExcelVVo(
            goodsName = "商品2",
            goodsStatus = GoodsStatusEnum.APPOINTMENT,
            price = BigDecimal(200)
        ))

        EasyExcel.write(outputStream, GoodsExcelVVo::class.java)
            .autoCloseStream(true)
            .sheet(sheetName)
            .doWrite(data)
    }

}