package com.zyf.runtime.support.captcha.service.config

import com.google.code.kaptcha.text.WordRenderer
import com.google.code.kaptcha.util.Configurable
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.util.*

/**
 * 验证码字体生成
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class CaptchaWordRenderer : Configurable(), WordRenderer {
    override fun renderWord(word: String, width: Int, height: Int): BufferedImage {
        val fontSize = config.textProducerFontSize
        val fonts = config.getTextProducerFonts(fontSize)
        val charSpace = config.textProducerCharSpace
        val image = BufferedImage(width, height, 2)

        val g2D = image.createGraphics()
        g2D.color = Color.WHITE
        val hints = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        hints.add(RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))
        g2D.setRenderingHints(hints)
        g2D.stroke = BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)

        val frc = g2D.fontRenderContext
        val random = Random()
        val startPosY = (height - fontSize) / 5 + fontSize
        val wordChars = word.toCharArray()
        val chosenFonts = arrayOfNulls<Font>(wordChars.size)
        val charWidths = IntArray(wordChars.size)
        var widthNeeded = 0

        var startPosX: Int
        startPosX = 0
        while (startPosX < wordChars.size) {
            chosenFonts[startPosX] = fonts[random.nextInt(fonts.size)]
            val charToDraw = charArrayOf(wordChars[startPosX])
            val gv = chosenFonts[startPosX]!!.createGlyphVector(frc, charToDraw)
            charWidths[startPosX] = gv.visualBounds.width.toInt()
            if (startPosX > 0) {
                widthNeeded += 2
            }

            widthNeeded += charWidths[startPosX]
            ++startPosX
        }

        startPosX = (width - widthNeeded) / 2

        for (i in wordChars.indices) {
            g2D.color = CaptchaColor.color
            g2D.font = chosenFonts[i]!!.deriveFont(Font.PLAIN)
            val charToDraw = charArrayOf(wordChars[i])
            g2D.drawChars(charToDraw, 0, charToDraw.size, startPosX, startPosY)
            startPosX = startPosX + charWidths[i] + charSpace
        }

        return image
    }
}
