package com.zyf.support

import cn.hutool.extra.servlet.JakartaServletUtil
import com.zyf.common.annotations.Body
import com.zyf.common.annotations.Operation
import com.zyf.common.constant.RequestHeaderConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.UserTypeEnum
import com.zyf.common.utils.SmartRequestUtil
import com.zyf.login.domain.RequestEmployee
import com.zyf.runtime.utils.SmartResponseUtil
import com.zyf.service.dto.FileQueryForm
import com.zyf.service.dto.FileVO
import com.zyf.support.domain.FileUploadVO
import com.zyf.support.service.FileService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * 文件服务
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@RestController
class FileInfoController(
    val fileService: FileService
) {

    /** 分页查询 @author 1024创新实验室-主任-卓大 */
    @Operation(summary = "分页查询 @author 1024创新实验室-主任-卓大")
    @PostMapping("/support/file/queryPage")
    fun queryPage(
        @Body pageBean: PageBean,
        @RequestBody queryForm: @Valid FileQueryForm
    ): ResponseDTO<PageResult<FileVO>> {
        return ResponseDTO.ok(fileService.queryPage(pageBean, queryForm))
    }


    /** 文件上传 @author 胡克 */
    @Operation(summary = "文件上传 @author 胡克")
    @PostMapping("/support/file/upload")
    fun upload(@RequestParam(value = "file") file: MultipartFile, @RequestParam folder: Int): ResponseDTO<FileUploadVO?> {

        val requestUser = SmartRequestUtil.requestUser!!
        return fileService.fileUpload(file, folder, requestUser)
    }

    /** 获取文件URL：根据fileKey @author 胡克 */
    @Operation(summary = "获取文件URL：根据fileKey @author 胡克")
    @GetMapping("/support/file/getFileUrl")
    fun getUrl(@RequestParam fileKey: String): ResponseDTO<String?> {
        return fileService.getFileUrl(fileKey)
    }

    /** 下载文件流（根据fileKey） @author 胡克 */
    @Operation(summary = "下载文件流（根据fileKey） @author 胡克")
    @GetMapping("/support/file/downLoad")
    @Throws(IOException::class)
    fun downLoad(@RequestParam fileKey: String, request: HttpServletRequest, response: HttpServletResponse) {
        val userAgent = JakartaServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT)
        val downloadFileResult = fileService.getDownloadFile(fileKey, userAgent)

        if (!downloadFileResult.ok) {
            SmartResponseUtil.write(response, downloadFileResult)
            return
        }

        // 下载文件信息
        val fileDownloadVO = downloadFileResult.data
        // 设置下载消息头
        SmartResponseUtil.setDownloadFileHeader(response, fileDownloadVO?.metadata?.fileName, fileDownloadVO?.metadata?.fileSize)
        // 下载
        fileDownloadVO?.data?.let { response.outputStream.write(it) }
    }
}
