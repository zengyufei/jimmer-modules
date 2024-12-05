package com.zyf.runtime.utils

import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSONUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.domain.ResponseDTO
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * 返回工具栏
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/11/25 18:51:32
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Slf4j
object SmartResponseUtil {

    fun write(response: HttpServletResponse, responseDTO: ResponseDTO<*>?) {
        // 重置response
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"

        try {
            response.writer.write(JSONUtil.toJsonStr(responseDTO))
            response.flushBuffer()
        } catch (ex: IOException) {
            log.error(ex.message, ex)
            throw RuntimeException(ex)
        }
    }

    fun setDownloadFileHeader(response: HttpServletResponse, fileName: String?) {
        setDownloadFileHeader(response, fileName, null)
    }

    fun setDownloadFileHeader(response: HttpServletResponse, fileName: String?, fileSize: Long?) {
        response.characterEncoding = "utf-8"
        try {
            if (fileSize != null) {
                response.setHeader(HttpHeaders.CONTENT_LENGTH, fileSize.toString())
            }

            if (StrUtil.isNotEmpty(fileName)) {
                response.setHeader(
                    HttpHeaders.CONTENT_TYPE,
                    MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM)
                        .toString() + ";charset=utf-8"
                )
                response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replace("\\+".toRegex(), "%20")
                )
                response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            }
        } catch (e: UnsupportedEncodingException) {
            log.error(e.message, e)
            throw RuntimeException(e)
        }
    }
}
