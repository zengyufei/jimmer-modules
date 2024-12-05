package com.zyf.cfg

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.zyf.support.service.FileStorageLocalServiceImpl
import com.zyf.support.service.IFileStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 文件上传 配置
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2019-09-02 23:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Configuration
class FileConfig : WebMvcConfigurer {
    @Value("\${file.storage.cloud.region}")
    lateinit var region: String

    @Value("\${file.storage.cloud.endpoint}")
    lateinit var endpoint: String

    @Value("\${file.storage.cloud.bucket-name}")
    lateinit var bucketName: String

    @Value("\${file.storage.cloud.access-key}")
    lateinit var accessKey: String

    @Value("\${file.storage.cloud.secret-key}")
    lateinit var secretKey: String

    @Value("\${file.storage.cloud.private-url-expire-seconds}")
    var privateUrlExpireSeconds: Long? = null

    @Value("\${file.storage.cloud.url-prefix}")
    lateinit var urlPrefix: String

    @Value("\${file.storage.local.upload-path}")
    lateinit var uploadPath: String

    @Value("\${file.storage.mode}")
    lateinit var mode: String

    /**
     * 初始化 云oss client 配置
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "file.storage", name = ["mode"], havingValue = "cloud")
    fun initAmazonS3(): AmazonS3 {
        val clientConfig = ClientConfiguration()
        clientConfig.protocol = Protocol.HTTPS
        return AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withClientConfiguration(clientConfig)
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withPathStyleAccessEnabled(false)
            .withChunkedEncodingDisabled(true)
            .build()
    }

//    @Bean
//    @ConditionalOnProperty(prefix = "file.storage", name = ["mode"], havingValue = MODE_CLOUD)
//    fun initCloudFileService(): IFileStorageService {
//        return FileStorageCloudServiceImpl()
//    }

    @Bean
    @ConditionalOnProperty(prefix = "file.storage", name = ["mode"], havingValue = MODE_LOCAL)
    fun initLocalFileService(): IFileStorageService {
        return FileStorageLocalServiceImpl()
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        if (MODE_LOCAL == mode) {
            val path = if (uploadPath.endsWith("/")) uploadPath else "$uploadPath/"
            registry.addResourceHandler(FileStorageLocalServiceImpl.UPLOAD_MAPPING + "/**").addResourceLocations("file:$path")
        }
    }

    companion object {
        private const val MODE_CLOUD = "cloud"

        private const val MODE_LOCAL = "local"
    }
}
