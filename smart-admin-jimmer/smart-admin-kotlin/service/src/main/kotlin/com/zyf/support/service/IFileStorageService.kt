package com.zyf.support.service

import com.zyf.common.domain.ResponseDTO
import com.zyf.support.domain.FileDownloadVO
import com.zyf.support.domain.FileUploadVO
import org.springframework.web.multipart.MultipartFile

/**
 * 接口
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
interface IFileStorageService {
    /**
     * 文件上传
     *
     * @param file
     * @param path
     * @return
     */
    fun upload(file: MultipartFile?, path: String?): ResponseDTO<FileUploadVO?>

    /**
     * 获取文件url
     *
     * @param fileKey
     * @return
     */
    fun getFileUrl(fileKey: String?): ResponseDTO<String?>


    /**
     * 流式下载（名称为原文件）
     *
     * @param key
     * @return
     */
    fun download(key: String?): ResponseDTO<FileDownloadVO?>

    /**
     * 单个删除文件
     * 根据文件key删除
     *
     * @param fileKey
     * @return
     */
    fun delete(fileKey: String?): ResponseDTO<String?>


    /**
     * 获取文件类型
     *
     * @param fileExt
     * @return
     */
    fun getContentType(fileExt: String?): String {
        // 文件的后缀名
        if ("bmp".equals(fileExt, ignoreCase = true)) {
            return "image/bmp"
        }
        if ("gif".equals(fileExt, ignoreCase = true)) {
            return "image/gif"
        }
        if ("jpeg".equals(fileExt, ignoreCase = true) || "jpg".equals(fileExt, ignoreCase = true)) {
            return "image/jpeg"
        }
        if ("png".equals(fileExt, ignoreCase = true)) {
            return "image/png"
        }
        if ("html".equals(fileExt, ignoreCase = true)) {
            return "text/html"
        }
        if ("txt".equals(fileExt, ignoreCase = true)) {
            return "text/plain"
        }
        if ("vsd".equals(fileExt, ignoreCase = true)) {
            return "application/vnd.visio"
        }
        if ("ppt".equals(fileExt, ignoreCase = true) || "pptx".equals(fileExt, ignoreCase = true)) {
            return "application/vnd.ms-powerpoint"
        }
        if ("doc".equals(fileExt, ignoreCase = true) || "docx".equals(fileExt, ignoreCase = true)) {
            return "application/msword"
        }
        if ("pdf".equals(fileExt, ignoreCase = true)) {
            return "application/pdf"
        }
        if ("xml".equals(fileExt, ignoreCase = true)) {
            return "text/xml"
        }
        return ""
    }
}
