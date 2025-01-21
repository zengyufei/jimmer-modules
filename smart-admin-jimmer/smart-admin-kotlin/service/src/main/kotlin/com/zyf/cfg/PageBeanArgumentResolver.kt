package com.zyf.cfg

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Body
import com.zyf.common.domain.PageBean
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * 解决 分页参数 注入问题
 */
class PageBeanArgumentResolver(
    private val pageNumKeyName: String = "pageNum",
    private val pageSizeKeyName: String = "pageSize",
    private val sortCodeKeyName: String = "sortCode",
    private val objectMapper: ObjectMapper
) : HandlerMethodArgumentResolver {

    /**
     * 判断Controller是否包含page参数
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (parameter.parameterType == PageBean::class.java) {
            return true
        }
        if (parameter.getParameterAnnotation(Body::class.java) != null) {
            return true
        }
        return false
    }

    /**
     * page 只支持查询 GET .如需解析POST获取请求报文体处理
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        // 支持多种参数传递方式（URL 参数和 JSON 请求体）
        // 采用了优雅的参数降级处理方式（JSON -> URL 参数 -> 默认值）

        // 从webRequest获取原生的HttpServletRequest对象
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)!!

        // 尝试从请求参数中获取分页和排序相关的参数
        val inputSortCode: String? = request.getParameter(sortCodeKeyName)
        val inputPageNum: String? = request.getParameter(pageNumKeyName)
        val inputPageSize: String? = request.getParameter(pageSizeKeyName)

        // 解析JSON字符串
        val jsonString = getHttpRequestBody(request)
        // 使用ObjectMapper将JSON字符串转换为JsonNode对象
        val jsonNode: JsonNode = objectMapper.readTree(jsonString)

        // 从JsonNode中获取分页和排序相关的参数
        val jsonSortCode: JsonNode? = jsonNode.get(sortCodeKeyName)
        val jsonPageNum: JsonNode? = jsonNode.get(pageNumKeyName)
        val jsonPageSize: JsonNode? = jsonNode.get(pageSizeKeyName)

        // 根据JSON和请求参数中的pageSize，选择有效的pageSize值
        val pageSize: Int? = try {
            jsonPageSize?.let {
                jsonPageSize.asInt().takeIf { it > 0 }
            } ?: inputPageSize?.toInt()?.takeIf { it > 0 }
        } catch (e: Exception) {
            null
        }

        // 根据JSON和请求参数中的pageNum，选择有效的pageNum值，并确保其至少为1
        val pageNum: Int = try {
            jsonPageNum?.let {
                jsonPageNum.asInt()
            } ?: inputPageNum?.toInt()?.let {
                1.coerceAtLeast(inputPageNum.toInt())
            } ?: 1
        } catch (e: Exception) {
            1  // 发生异常时返回默认值 1
        }

        // 根据JSON和请求参数中的sortCode，选择有效的sortCode值
        val sortCode: String? = jsonSortCode?.let {
            jsonSortCode.asText()?.takeIf { it.isNotBlank() }
        } ?: inputSortCode

        // 如果pageSize为null，则返回null，表示无法创建PageBean对象
        pageSize ?: return null

        // 使用有效的参数创建并返回PageBean对象
        return PageBean.of(pageNum, pageSize, sortCode)
    }


    private fun getHttpRequestBody(request: HttpServletRequest?): String {
        if (request!!.method.equals("get", ignoreCase = true)) {
            return request.queryString
        } else {
            if (request is RequestWrapper) {
                return request.getHttpBodyStr()
            }
        }
        println(String.format("request 非 CachingRequest %s", request.javaClass))
        return ""
    }
} 