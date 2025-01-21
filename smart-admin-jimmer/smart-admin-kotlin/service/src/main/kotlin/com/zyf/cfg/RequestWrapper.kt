package com.zyf.cfg

import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.springframework.util.ObjectUtils
import org.springframework.util.StreamUtils
import org.springframework.web.util.HtmlUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * Request包装类
 *
 * 1.预防xss攻击
 * 2.拓展requestbody无限获取(HttpServletRequestWrapper只能获取一次)
 *
 * @author Caratacus
 */
@Slf4j
class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    /**
     * 存储requestBody byte[]
     */
    private var body: ByteArray = getByteBody()
    private val parameterMap: Map<String, Array<String>> = super.getParameterMap()


    override fun getReader(): BufferedReader? {
        val inputStream = inputStream
        return inputStream?.let { BufferedReader(InputStreamReader(it)) }
    }

    override fun getInputStream(): ServletInputStream? {
        if (ObjectUtils.isEmpty(body)) {
            return null
        }
        val bais = ByteArrayInputStream(body)
        val value = object : ServletInputStream() {
            override fun isFinished(): Boolean = false

            override fun isReady(): Boolean = false

            override fun setReadListener(readListener: ReadListener?) {}

            override fun read(): Int = bais.read()
        }
        clearBody()
        return value
    }

    override fun getParameterValues(name: String): Array<String>? {
        val values = super.getParameterValues(name) ?: return null
        return Array(values.size) { i -> htmlEscape(values[i]) }
    }

    override fun getParameter(name: String): String? {
        return super.getParameter(name)?.let { htmlEscape(it) }
    }

    /**
     * 重写 getParameterMap() 方法 解决 undertow 中流被读取后，会进行标记，从而导致无法正确获取 body 中的表单数据的问题
     */
    override fun getParameterMap(): Map<String, Array<String>> {
        return parameterMap
    }

    override fun getAttribute(name: String): Any? {
        val value = super.getAttribute(name)
        return if (value is String) {
            htmlEscape(value)
        } else value
    }

    override fun getHeader(name: String): String? {
        return super.getHeader(name)?.let { htmlEscape(it) }
    }

    override fun getQueryString(): String? {
        return super.getQueryString()?.let { htmlEscape(it) }
    }

    /**
     * 使用spring HtmlUtils 转义html标签达到预防xss攻击效果
     */
    private fun htmlEscape(str: String): String {
        return HtmlUtils.htmlEscape(str)
    }

    /**
     * 获取请求体
     */
    private fun getByteBody(): ByteArray {
        return try {
            StreamUtils.copyToByteArray(request.inputStream)
        } catch (e: Exception) {
            log.error("Error: Get RequestBody byte[] fail", e)
            ByteArray(0)
        }
    }

    /**
     * 获取请求体
     */
    fun getHttpBody(): ByteArray {
        return body
    }

    /**
     * 获取请求体
     */
    fun getHttpBodyStr(): String {
        return if (body.isEmpty()) "" else String(body)
    }

    /**
     * 清理缓存的请求体数据
     */
    private fun clearBody() {
        // 将body数组清空
        body = ByteArray(0)
    }
}