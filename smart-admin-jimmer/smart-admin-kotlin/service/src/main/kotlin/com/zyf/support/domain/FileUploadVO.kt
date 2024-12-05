package com.zyf.support.domain


/**
 * 文件信息
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019年10月11日 15:34:47
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class FileUploadVO {
    /** 文件id  */
    var fileId: String? = null

    /** 文件名称  */
    var fileName: String? = null

    /** fileUrl  */
    var fileUrl: String? = null

    /** fileKey  */
    var fileKey: String? = null

    /** 文件大小  */
    var fileSize: Long? = null

    /** 文件类型  */
    var fileType: String? = null
}
