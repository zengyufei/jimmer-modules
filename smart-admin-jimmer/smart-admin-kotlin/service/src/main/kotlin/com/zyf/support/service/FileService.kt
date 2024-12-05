package com.zyf.support.service

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.code.UserErrorCode
import com.zyf.common.constant.StringConst
import com.zyf.common.domain.PageBean
import com.zyf.common.domain.PageResult
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.FileFolderTypeEnum
import com.zyf.common.jimmer.orderBy
import com.zyf.common.jimmer.page
import com.zyf.common.utils.SmartEnumUtil
import com.zyf.common.domain.RequestUser
import com.zyf.repository.FileRepository
import com.zyf.runtime.support.redis.RedisService
import com.zyf.service.dto.FileQueryForm
import com.zyf.service.dto.FileVO
import com.zyf.support.FileInfo
import com.zyf.support.createTime
import com.zyf.support.domain.FileDownloadVO
import com.zyf.support.domain.FileUploadVO
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.compress.utils.Lists
import org.apache.commons.lang3.StringUtils
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * 文件服务
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)
 */
@Service
class FileService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    var fileRepository: FileRepository,
    val redisService: RedisService,
    val securityFileService: SecurityFileService,
    val fileStorageService: IFileStorageService,
) {

    /**
     * 文件上传服务
     *
     * @param file
     * @param inputFileType 文件夹类型
     * @return
     */
    fun fileUpload(file: MultipartFile?, inputFileType: Int?, requestUser: RequestUser?): ResponseDTO<FileUploadVO?> {
        val folderTypeEnum: FileFolderTypeEnum = SmartEnumUtil.getEnumByValue<FileFolderTypeEnum>(inputFileType) ?: return ResponseDTO.userErrorParam("文件夹错误")

        if (null == file || file.size == 0L) {
            return ResponseDTO.userErrorParam("上传文件不能为空")
        }

        // 校验文件名称
        val originalFilename: String? = file.originalFilename
        if (originalFilename == null || StrUtil.isBlank(originalFilename)) {
            return ResponseDTO.userErrorParam("上传文件名称不能为空")
        }

        if (originalFilename.length > FILE_NAME_MAX_LENGTH) {
            return ResponseDTO.userErrorParam("文件名称最大长度为：$FILE_NAME_MAX_LENGTH")
        }

        // 校验文件大小以及安全性
        val validateFile: ResponseDTO<String?> = securityFileService.checkFile(file)
        if (!validateFile.ok) {
            return ResponseDTO.error(validateFile)
        }

        // 进行上传
        val response: ResponseDTO<FileUploadVO?> = fileStorageService.upload(file, folderTypeEnum.folder)
        if (!response.ok) {
            return response
        }

        // 上传成功 保存记录数据库
        val uploadVO: FileUploadVO = response.data!!
        val fileInfo = FileInfo {
            folderType = folderTypeEnum.value
            fileName = originalFilename
            fileSize = file.size
            uploadVO.fileKey?.let { fileKey = it }
            uploadVO.fileType?.let { fileType = it }
            creatorId = requestUser?.userId
            creatorName = requestUser?.userName
            creatorUserType = requestUser?.userType?.value
        }
        val result = sql.insert(fileInfo)

        // 将fileId 返回给前端
        uploadVO.fileId = result.modifiedEntity.fileId

        return response
    }

    /**
     * 批量获取文件信息
     *
     * @param fileKeyList
     * @return
     */
    fun getFileList(fileKeyList: List<String?>): List<FileVO> {
        if (CollectionUtils.isEmpty(fileKeyList)) {
            return Lists.newArrayList()
        }

        // 查询数据库，并获取 file url
        val fileList: List<FileVO> = fileRepository.byFileKeys(fileKeyList) {
            select(table.fetch(FileVO::class))
        }

        return fileList.map {
            val fileUrlResponse: ResponseDTO<String?> = fileStorageService.getFileUrl(it.fileKey)
            if (fileUrlResponse.ok) {
                it.copy(
                    fileUrl = fileUrlResponse.data
                )
            } else {
                it
            }
        }
    }


    /**
     * 根据文件绝对路径 获取文件URL
     * 支持单个 key 逗号分隔的形式
     *
     * @param fileKeys
     * @return
     */
    fun getFileUrl(fileKeys: String?): ResponseDTO<String?> {
        if (StringUtils.isBlank(fileKeys)) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR)
        }

        val fileKeyArray: List<String> = StrUtil.split(fileKeys, StringConst.SEPARATOR)
        val fileUrlList: MutableList<String> = mutableListOf()
        for (fileKey in fileKeyArray) {
            val fileUrlResponse: ResponseDTO<String?> = fileStorageService.getFileUrl(fileKey)
            if (fileUrlResponse.ok) {
                fileUrlResponse.data?.let { fileUrlList.add(it) }
            }
        }
        return ResponseDTO.ok(StrUtil.join(StringConst.SEPARATOR, fileUrlList))
    }


    /**
     * 根据文件服务类型 和 FileKey 下载文件
     */
    fun getDownloadFile(fileKey: String?, userAgent: String?): ResponseDTO<FileDownloadVO?> {
        val fileVO: FileVO = fileRepository.byFileKey(fileKey) { select(table.fetch(FileVO::class)) } ?: return ResponseDTO.userErrorParam("文件不存在")

        // 根据文件服务类 获取对应文件服务 查询 url
        val download: ResponseDTO<FileDownloadVO?> = fileStorageService.download(fileKey)
        if (download.ok) {
            download.data?.metadata?.fileName = fileVO.fileName
        }
        return download
    }

    /**
     * 分页查询
     */
    fun queryPage(pageBean: PageBean, queryForm: FileQueryForm?): PageResult<FileVO> {
        val pageResult = sql.createQuery(FileInfo::class) {
            pageBean.sortCode?.let {
                orderBy(pageBean)
            } ?: orderBy(table.createTime.desc())

            where(queryForm)
            select(table.fetch(FileVO::class))
        }.page(pageBean)
        return pageResult
    }


    companion object {
        /**
         * 文件名最大长度
         */
        private const val FILE_NAME_MAX_LENGTH = 100
    }
}
