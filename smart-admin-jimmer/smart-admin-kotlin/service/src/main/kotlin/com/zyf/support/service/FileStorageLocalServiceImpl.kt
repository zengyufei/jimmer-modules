package com.zyf.support.service

import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.net.NetUtil
import cn.hutool.core.util.StrUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.code.SystemErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.support.domain.FileDownloadVO
import com.zyf.support.domain.FileMetadataVO
import com.zyf.support.domain.FileUploadVO
import jakarta.annotation.PostConstruct
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.FileCopyUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*

/**
 * 本地存储 实现
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright 1024创新实验室 （ [1024创新实验室](https://1024lab.net) ）
 */
@Slf4j
class FileStorageLocalServiceImpl : IFileStorageService {
    @Value("\${file.storage.local.upload-path}")
    private val uploadPath: String? = null

    @Value("\${file.storage.local.url-prefix}")
    private var urlPrefix: String? = null

    @Value("\${server.servlet.context-path}")
    private val contextPath: String? = null

    @Value("\${server.port}")
    private val port: String? = null

    @PostConstruct
    fun initUrlPrefix() {
        if (StrUtil.isNotEmpty(urlPrefix)) {
            return
        }

        val localhostIp = NetUtil.getLocalhostStr()
        var finalContextPath = if (contextPath!!.startsWith("/")) contextPath else "/$contextPath"
        if (finalContextPath.endsWith("/")) {
            finalContextPath = finalContextPath.substring(0, finalContextPath.length - 1)
        }
        urlPrefix = "http://$localhostIp:$port$finalContextPath$UPLOAD_MAPPING"
        urlPrefix = if (urlPrefix!!.endsWith("/")) urlPrefix else "$urlPrefix/"
    }

    override fun upload(file: MultipartFile?, path: String?): ResponseDTO<FileUploadVO?> {
        var newPath = path
        if (null == file) {
            return ResponseDTO.userErrorParam("上传文件不能为空")
        }
        val filePath = uploadPath + newPath
        val directory = File(filePath)
        if (!directory.exists()) {
            // 目录不存在，新建
            directory.mkdirs()
        }
        if (!newPath!!.endsWith("/")) {
            newPath = "$newPath/"
        }
        val fileUploadVO = FileUploadVO()
        // 原文件名
        val originalFileName = file.originalFilename
        // 新文件名
        val uuid = UUID.randomUUID().toString().replace("-".toRegex(), "")
        val time = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_FORMATTER)
        var newFileName = uuid + "_" + time
        val fileType = FilenameUtils.getExtension(originalFileName)
        if (StrUtil.isNotEmpty(fileType)) {
            newFileName = "$newFileName.$fileType"
        }
        // 生成文件key
        val fileKey = newPath + newFileName
        // 创建文件
        val fileTemp = File(File(filePath + newFileName).absolutePath)
        try {
            file.transferTo(fileTemp)
            fileUploadVO.fileUrl = this.generateFileUrl(fileKey)
            fileUploadVO.fileName = newFileName
            fileUploadVO.fileKey = fileKey
            fileUploadVO.fileSize = file.size
            fileUploadVO.fileType = FilenameUtils.getExtension(originalFileName)
        } catch (e: IOException) {
            if (fileTemp.exists() && fileTemp.isFile) {
                fileTemp.delete()
            }
            log.error("", e)
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "上传失败")
        }
        return ResponseDTO.ok(fileUploadVO)
    }

    /**
     * 生成fileUrl地址
     *
     * @param filePath
     * @return
     */
    private fun generateFileUrl(filePath: String?): String {
        return urlPrefix + filePath
    }

    /**
     * 获取文件Url
     *
     * @param fileKey
     * @return
     */
    override fun getFileUrl(fileKey: String?): ResponseDTO<String?> {
        if (StringUtils.isBlank(fileKey)) {
            return ResponseDTO.userErrorParam("文件不存在，key为空")
        }

        val fileUrl = this.generateFileUrl(fileKey)
        return ResponseDTO.ok(fileUrl)
    }

    /**
     * 文件下载
     *
     * @param inputFileKey
     * @return
     */
    override fun download(inputFileKey: String?): ResponseDTO<FileDownloadVO?> {
        val filePath = uploadPath + inputFileKey
        val localFile = File(filePath)
        var ins: InputStream? = null
        try {
            ins = Files.newInputStream(localFile.toPath())
            // 输入流转换为字节流
            val buffer = FileCopyUtils.copyToByteArray(ins)
            val fileDownloadVO = FileDownloadVO()
            fileDownloadVO.data = buffer

            val fileMetadataDTO = FileMetadataVO()
            fileMetadataDTO.fileName = localFile.name
            fileMetadataDTO.fileSize = localFile.length()
            fileMetadataDTO.fileFormat = FilenameUtils.getExtension(localFile.name)
            fileDownloadVO.metadata = fileMetadataDTO

            return ResponseDTO.ok(fileDownloadVO)
        } catch (e: IOException) {
            log.error("文件下载-发生异常：", e)
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "文件下载失败")
        } finally {
            try {
                // 关闭输入流
                ins?.close()
            } catch (e: IOException) {
                log.error("文件下载-发生异常：", e)
            }
        }
    }

    override fun delete(fileKey: String?): ResponseDTO<String?> {
        val filePath = uploadPath + fileKey
        val localFile = File(filePath)
        try {
            FileUtils.forceDelete(localFile)
        } catch (e: IOException) {
            log.error("删除本地文件失败：{}", filePath, e)
        }
        return ResponseDTO.ok()
    }

    companion object {
        const val UPLOAD_MAPPING: String = "/upload"
    }
}
