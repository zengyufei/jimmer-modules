// package com.zyf.support.service
//
// import com.amazonaws.services.s3.AmazonS3
// import com.amazonaws.services.s3.model.CannedAccessControlList
// import com.amazonaws.services.s3.model.ObjectMetadata
// import com.zyf.cfg.FileConfig
// import jakarta.annotation.Resource
// import org.apache.commons.collections4.MapUtils
// import org.apache.commons.io.FilenameUtils
// import org.apache.commons.lang3.StringUtils
// import org.springframework.util.FileCopyUtils
// import java.io.IOException
// import java.io.InputStream
// import java.io.UnsupportedEncodingException
// import java.net.URLEncoder
// import java.nio.charset.StandardCharsets
// import java.time.LocalDateTime
// import java.util.*
//
// /**
//  * 云计算 实现
//  *
//  * @Author 1024创新实验室: 罗伊
//  * @Date 2019年10月11日 15:34:47
//  * @Wechat zhuoda1024
//  * @Email lab1024@163.com
//  * @Copyright  [1024创新实验室](https://1024lab.net)
//  */
// @Slf4j
// class FileStorageCloudServiceImpl : IFileStorageService {
//     @Resource
//     private val amazonS3: AmazonS3? = null
//
//     @Resource
//     private val cloudConfig: FileConfig? = null
//
//     @Resource
//     private val redisService: RedisService? = null
//
//     @Resource
//     private val fileDao: FileDao? = null
//
//     override fun upload(file: MultipartFile?, path: String?): ResponseDTO<FileUploadVO?>? {
//         // 设置文件 key
//         val originalFileName: String = file.getOriginalFilename()
//         if (SmartStringUtil.isEmpty(originalFileName)) {
//             return ResponseDTO.userErrorParam("上传文件名为空")
//         }
//
//         val fileType = FilenameUtils.getExtension(originalFileName)
//         val uuid = UUID.randomUUID().toString().replace("-".toRegex(), "")
//         val time: String = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_FORMATTER)
//         val fileKey = path + uuid + "_" + time + "." + fileType
//
//         // 文件名称 URL 编码
//         val urlEncoderFilename: String
//         try {
//             urlEncoderFilename = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.name())
//         } catch (e: UnsupportedEncodingException) {
//             log.error("文件上传服务URL ENCODE-发生异常：", e)
//             return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "上传失败")
//         }
//         val meta = ObjectMetadata()
//         meta.contentEncoding = StandardCharsets.UTF_8.name()
//         meta.contentDisposition = "attachment;filename=$urlEncoderFilename"
//         val userMetadata: MutableMap<String, String> = HashMap(10)
//         userMetadata[USER_METADATA_FILE_NAME] = urlEncoderFilename
//         userMetadata[USER_METADATA_FILE_FORMAT] = fileType
//         userMetadata[USER_METADATA_FILE_SIZE] = file.getSize().toString()
//         meta.userMetadata = userMetadata
//         meta.contentLength = file.getSize()
//         meta.contentType = getContentType(fileType)
//         try {
//             amazonS3!!.putObject(cloudConfig.getBucketName(), fileKey, file.getInputStream(), meta)
//         } catch (e: IOException) {
//             log.error("文件上传-发生异常：", e)
//             return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "上传失败")
//         }
//         // 根据文件路径获取并设置访问权限
//         val acl = this.getACL(path)
//         amazonS3.setObjectAcl(cloudConfig.getBucketName(), fileKey, acl)
//         // 返回上传结果
//         val uploadVO: FileUploadVO = FileUploadVO()
//         uploadVO.setFileName(originalFileName)
//         uploadVO.setFileType(fileType)
//         // 根据 访问权限 返回不同的 URL
//         var url: String = cloudConfig.getUrlPrefix() + fileKey
//         if (CannedAccessControlList.Private == acl) {
//             // 获取临时访问的URL
//             url = getFileUrl(fileKey).getData()
//         }
//         uploadVO.setFileUrl(url)
//         uploadVO.setFileKey(fileKey)
//         uploadVO.setFileSize(file.getSize())
//         return ResponseDTO.ok(uploadVO)
//     }
//
//     /**
//      * 获取文件url
//      *
//      * @param fileKey
//      * @return
//      */
//     override fun getFileUrl(fileKey: String?): ResponseDTO<String?>? {
//         if (StringUtils.isBlank(fileKey)) {
//             return ResponseDTO.userErrorParam("文件不存在，key为空")
//         }
//
//         if (!fileKey.startsWith(FileFolderTypeEnum.FOLDER_PRIVATE)) {
//             // 不是私有的 都公共读
//             return ResponseDTO.ok(cloudConfig.getUrlPrefix() + fileKey)
//         }
//
//         // 如果是私有的，则规定时间内可以访问，超过规定时间，则连接失效
//         val fileRedisKey: String = RedisKeyConst.Support.FILE_PRIVATE_VO + fileKey
//         var fileVO: FileVO = redisService.getObject(fileRedisKey, FileVO::class.java)
//         if (fileVO == null) {
//             fileVO = fileDao.getByFileKey(fileKey)
//             if (fileVO == null) {
//                 return ResponseDTO.userErrorParam("文件不存在")
//             }
//
//             val expiration: Date = Date(System.currentTimeMillis() + cloudConfig.getPrivateUrlExpireSeconds() * 1000L)
//             val url = amazonS3!!.generatePresignedUrl(cloudConfig.getBucketName(), fileKey, expiration)
//             fileVO.setFileUrl(url.toString())
//             redisService.set(fileRedisKey, fileVO, cloudConfig.getPrivateUrlExpireSeconds() - 5)
//         }
//
//         return ResponseDTO.ok(fileVO.getFileUrl())
//     }
//
//
//     /**
//      * 流式下载（名称为原文件）
//      */
//     override fun download(key: String?): ResponseDTO<FileDownloadVO?>? {
//         // 获取oss对象
//         val s3Object = amazonS3!!.getObject(cloudConfig.getBucketName(), key)
//         // 获取文件 meta
//         val metadata = s3Object.objectMetadata
//         val userMetadata = metadata.userMetadata
//         var metadataDTO: FileMetadataVO? = null
//         if (MapUtils.isNotEmpty(userMetadata)) {
//             metadataDTO = FileMetadataVO()
//             metadataDTO.setFileFormat(userMetadata[USER_METADATA_FILE_FORMAT])
//             metadataDTO.setFileName(userMetadata[USER_METADATA_FILE_NAME])
//             val fileSizeStr = userMetadata[USER_METADATA_FILE_SIZE]
//             val fileSize = if (StringUtils.isBlank(fileSizeStr)) null else fileSizeStr!!.toLong()
//             metadataDTO.setFileSize(fileSize)
//         }
//
//         // 获得输入流
//         val objectContent: InputStream = s3Object.objectContent
//         try {
//             // 输入流转换为字节流
//             val buffer = FileCopyUtils.copyToByteArray(objectContent)
//
//             val fileDownloadVO: FileDownloadVO = FileDownloadVO()
//             fileDownloadVO.setData(buffer)
//             fileDownloadVO.setMetadata(metadataDTO)
//             return ResponseDTO.ok(fileDownloadVO)
//         } catch (e: IOException) {
//             log.error("文件下载-发生异常：", e)
//             return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "下载失败")
//         } finally {
//             try {
//                 // 关闭输入流
//                 objectContent.close()
//                 s3Object.close()
//             } catch (e: IOException) {
//                 log.error("文件下载-发生异常：", e)
//             }
//         }
//     }
//
//     /**
//      * 根据文件夹路径 返回对应的访问权限
//      *
//      * @param fileKey
//      * @return
//      */
//     private fun getACL(fileKey: String?): CannedAccessControlList {
//         // 公用读
//         if (fileKey.contains(FileFolderTypeEnum.FOLDER_PUBLIC)) {
//             return CannedAccessControlList.PublicRead
//         }
//         // 其他默认私有读写
//         return CannedAccessControlList.Private
//     }
//
//     /**
//      * 单个删除文件
//      * 根据 file key 删除文件
//      * ps：不能删除fileKey不为空的文件夹
//      *
//      * @param fileKey 文件or文件夹
//      * @return
//      */
//     override fun delete(fileKey: String?): ResponseDTO<String?>? {
//         amazonS3!!.deleteObject(cloudConfig.getBucketName(), fileKey)
//         return ResponseDTO.ok()
//     }
//
//     companion object {
//         /**
//          * 自定义元数据 文件名称
//          */
//         private const val USER_METADATA_FILE_NAME = "file-name"
//
//         /**
//          * 自定义元数据 文件格式
//          */
//         private const val USER_METADATA_FILE_FORMAT = "file-format"
//
//         /**
//          * 自定义元数据 文件大小
//          */
//         private const val USER_METADATA_FILE_SIZE = "file-size"
//     }
// }
