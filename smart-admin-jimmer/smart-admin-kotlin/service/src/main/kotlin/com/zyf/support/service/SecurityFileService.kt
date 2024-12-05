package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.domain.ResponseDTO
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * 三级等保 文件上传 相关
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2024/08/22 19:25:59
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Service
class SecurityFileService (
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
    val level3ProtectConfigService: Level3ProtectConfigService,
) {


    /**
     * 检测文件安全类型
     */
    fun checkFile(file: MultipartFile): ResponseDTO<String?> {
        // 检验文件大小

        if (level3ProtectConfigService.maxUploadFileSizeMb > 0) {
            val maxSize = level3ProtectConfigService.maxUploadFileSizeMb * 1024 * 1024
            if (file.size > maxSize) {
                return ResponseDTO.userErrorParam("上传文件最大为:" + level3ProtectConfigService.maxUploadFileSizeMb + " mb")
            }
        }

        // 文件类型安全检测
        if (!level3ProtectConfigService.isFileDetectFlag) {
            return ResponseDTO.ok()
        }

        // 检测文件类型
        // .....
        return ResponseDTO.ok()
    }
}
