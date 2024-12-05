package com.zyf.runtime.utils

import com.alibaba.excel.EasyExcel
import com.alibaba.excel.write.handler.SheetWriteHandler
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.runtime.utils.SmartResponseUtil.setDownloadFileHeader
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.openxml4j.opc.TargetMode
import org.apache.poi.xssf.usermodel.XSSFRelation
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JLabel
import kotlin.Double
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.Throws
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * excel 工具类
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2024/4/22 22:49:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright 1024创新实验室 （ https://1024lab.net ），2012-2024
 */
object SmartExcelUtil {
    /**
     * 通用单sheet导出
     */
    @Throws(IOException::class)
    fun exportExcel(
        response: HttpServletResponse,
        fileName: String?,
        sheetName: String?,
        head: Class<*>?,
        data: Collection<*>?
    ) {
        // 设置下载消息头
        setDownloadFileHeader(response, fileName, null)
        // 下载
        EasyExcel.write(response.outputStream, head)
            .autoCloseStream(false)
            .sheet(sheetName)
            .doWrite(data)
    }

    /**
     * 通用单 sheet水印 导出
     */
    @Throws(IOException::class)
    fun exportExcelWithWatermark(
        response: HttpServletResponse,
        fileName: String?,
        sheetName: String?,
        head: Class<*>?,
        data: Collection<*>?,
        watermarkString: String
    ) {
        // 设置下载消息头
        setDownloadFileHeader(response, fileName, null)
        // 水印
        val watermark = Watermark(watermarkString)
        // 一定要inMemory
        EasyExcel.write(response.outputStream, head)
            .inMemory(true)
            .sheet(sheetName)
            .registerWriteHandler(CustomWaterMarkHandler(watermark))
            .doWrite(data)
    }


    @Slf4j
    private class CustomWaterMarkHandler(private val watermark: Watermark) : SheetWriteHandler {
        override fun afterSheetCreate(writeWorkbookHolder: WriteWorkbookHolder, writeSheetHolder: WriteSheetHolder) {
            val bufferedImage = createWatermarkImage()
            val workbook = writeSheetHolder.parentWriteWorkbookHolder.workbook as XSSFWorkbook
            try {
                // 添加水印的具体操作
                addWatermarkToSheet(workbook, bufferedImage)
            } catch (e: Exception) {
                log.error("添加水印出错:", e)
            }
        }

        /**
         * 创建水印图片
         *
         * @return
         */
        private fun createWatermarkImage(): BufferedImage {
            // 获取水印相关参数
            val font: Font = watermark.font
            val width: Int = watermark.width
            val height: Int = watermark.height
            val color: Color = watermark.color
            val text: String = watermark.content

            // 创建带有透明背景的 BufferedImage
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val g = image.createGraphics()

            // 设置画笔字体、平滑、颜色
            g.font = font
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.color = color

            // 计算水印位置和角度
            val y: Int = watermark.yAxis
            val x: Int = watermark.xAxis
            val transform = AffineTransform.getRotateInstance(Math.toRadians(-watermark.angle), 0.0, y.toDouble())
            g.transform = transform
            // 绘制水印文字
            g.drawString(text, x, y)

            // 释放资源
            g.dispose()

            return image
        }

        private fun addWatermarkToSheet(workbook: XSSFWorkbook, watermarkImage: BufferedImage) {
            try {
                ByteArrayOutputStream().use { os ->
                    ImageIO.write(watermarkImage, "png", os)
                    val pictureIdx = workbook.addPicture(os.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG)
                    val pictureData = workbook.allPictures[pictureIdx]
                    for (i in 0 until workbook.numberOfSheets) {
                        // 获取每个Sheet表
                        val sheet = workbook.getSheetAt(i)
                        val ppn = pictureData.packagePart.partName
                        val relType = XSSFRelation.IMAGES.relation
                        val pr = sheet.packagePart.addRelationship(ppn, TargetMode.INTERNAL, relType, null)
                        sheet.ctWorksheet.addNewPicture().id = pr.id
                    }
                }
            } catch (e: Exception) {
                // 处理ImageIO.write可能抛出的异常
                log.error("添加水印图片时发生错误", e)
            }
        }
    }

    private class Watermark {
        constructor(content: String) {
            this.content = content
            init()
        }

        constructor(content: String, color: Color, font: Font, angle: Double) {
            this.content = content
            this.color = color
            this.font = font
            this.angle = angle
            init()
        }

        /**
         * 根据水印内容长度自适应水印图片大小，简单的三角函数
         */
        private fun init() {
            val fontMetrics = JLabel().getFontMetrics(this.font)
            val stringWidth = fontMetrics.stringWidth(this.content)
            val charWidth = fontMetrics.charWidth('A')
            this.width = abs(stringWidth * cos(Math.toRadians(this.angle)))
                .toInt() + 5 * charWidth
            this.height = abs(stringWidth * sin(Math.toRadians(this.angle)))
                .toInt() + 5 * charWidth
            this.yAxis = this.height
            this.xAxis = charWidth
        }

        /**
         * 水印内容
         */
         var content: String

        /**
         * 画笔颜色
         */
         var color = Color(239, 239, 239)

        /**
         * 字体样式
         */
         var font = Font("Microsoft YaHei", Font.BOLD, 26)

        /**
         * 水印宽度
         */
         var width = 0

        /**
         * 水印高度
         */
         var height = 0

        /**
         * 倾斜角度，非弧度制
         */
         var angle = 25.0

        /**
         * 字体的y轴位置
         */
         var yAxis = 50

        /**
         * 字体的X轴位置
         */
         var xAxis = 0

        /**
         * 水平倾斜度
         */
         val shearX = 0.1

        /**
         * 垂直倾斜度
         */
         val shearY = -0.26
    }
}
