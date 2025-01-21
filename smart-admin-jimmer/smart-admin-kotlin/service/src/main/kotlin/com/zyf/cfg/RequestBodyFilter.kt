package com.zyf.cfg

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * 重新组装 HttpServletRequest 返回, 解决拦截器中从流中获取完 post 请求中的 body 参数，controller 层无法再次获取的问题
 */
//@Order(10)
//@Component
class RequestBodyFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {

        val method = request.method
        val contentType = request.contentType ?: ""

        if (HttpMethod.POST.name() == method && contentType.startsWith("multipart/")) {
            // 文件上传类型
            chain.doFilter(request, response);
        } else if (HttpMethod.POST.name() == method && contentType.startsWith("application/json")) {
            // 重新生成ServletRequest  这个新的 ServletRequest 获取流时会将流的数据重写进流里面
            chain.doFilter(RequestWrapper(request), response);
        } else {
            chain.doFilter(request, response)
        }
    }
}