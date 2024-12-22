package com.zyf.login.service

import cn.hutool.core.lang.UUID
import cn.hutool.core.util.StrUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.base.SystemEnvironment
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.MailTemplateTypeEnum
import com.zyf.login.enums.MailTemplateCodeEnum
import com.zyf.repository.support.MailTemplateRepository
import com.zyf.support.MailTemplate
import freemarker.cache.StringTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import jakarta.annotation.Resource
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File
import java.io.StringWriter
import java.io.Writer
import java.util.*


/**
 *
 * 发生邮件：<br></br>
 * 1、支持直接发送 <br></br>
 * 2、支持使用邮件模板发送
 *
 * @Author 1024创新实验室-创始人兼主任:卓大
 * @Date 2024/8/5
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net) ，Since 2012
 */
@Slf4j
@Component
class MailService(
    val javaMailSender: JavaMailSender,
    val mailTemplateRepository: MailTemplateRepository,
    val systemEnvironment: SystemEnvironment,
    @Value("\${spring.mail.username}")
     val clientMail: String,
) {


    /**
     * 使用模板发送邮件
     */
    /**
     * 使用模板发送邮件
     */
    @JvmOverloads
    fun sendMail(templateCode: MailTemplateCodeEnum, templateParamsMap: Map<String, Any>, receiverUserList: List<String?>, fileList: List<File?>? = null): ResponseDTO<String?> {
        val mailTemplate: MailTemplate = mailTemplateRepository.byId(templateCode.name.lowercase(Locale.getDefault())) ?: return ResponseDTO.userErrorParam("模版不存在")

        if (mailTemplate.disableFlag) {
            return ResponseDTO.userErrorParam("模版已禁用，无法发送")
        }
        val content: String = if (MailTemplateTypeEnum.FREEMARKER.value == mailTemplate.templateType.trim()) {
            freemarkerResolverContent(mailTemplate.templateContent, templateParamsMap)
        } else if (MailTemplateTypeEnum.STRING.value == mailTemplate.templateType.trim()) {
            stringResolverContent(mailTemplate.templateContent, templateParamsMap)
        } else {
            return ResponseDTO.userErrorParam("模版类型不存在")
        }

        try {
            this.sendMail(mailTemplate.templateSubject, content, fileList, receiverUserList, true)
        } catch (e: Throwable) {
            log.error("邮件发送失败", e)
            return ResponseDTO.userErrorParam("邮件发送失败")
        }
        return ResponseDTO.ok()
    }


    /**
     * 发送邮件
     *
     * @param subject          主题
     * @param content          内容
     * @param fileList         文件
     * @param receiverUserList 接收方
     * @throws MessagingException
     */
    @Throws(MessagingException::class)
    fun sendMail(subject: String, content: String?, fileList: List<File?>?, receiverUserList: List<String?>, isHtml: Boolean) {
        var subject = subject
        if (CollectionUtils.isEmpty(receiverUserList)) {
            throw RuntimeException("接收方不能为空")
        }

        if (StringUtils.isBlank(content)) {
            throw RuntimeException("邮件内容不能为空")
        }

        if (!systemEnvironment!!.isProd) {
            subject = "(测试)$subject"
        }

        val mimeMessage: MimeMessage = javaMailSender!!.createMimeMessage()

        //是否为多文件上传
        val multiparty = !CollectionUtils.isEmpty(fileList)
        val helper = MimeMessageHelper(mimeMessage, multiparty)
        helper.setFrom(clientMail!!)
        helper.setTo(receiverUserList.toTypedArray<String?>())
        helper.setSubject(subject)
        //发送html格式
        helper.setText(content!!, isHtml)

        //附件
        if (multiparty) {
            for (file in fileList!!) {
                helper.addAttachment(file!!.name, file)
            }
        }
        javaMailSender.send(mimeMessage)
    }

    /**
     * 使用字符串生成最终内容
     */
    private fun stringResolverContent(stringTemplate: String, templateParamsMap: Map<String, Any>): String {
        val contractHtml: String = StrUtil.format(stringTemplate, templateParamsMap)
        val doc: Document = Jsoup.parse(contractHtml)
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return doc.outerHtml()
    }


    /**
     * 使用 freemarker 生成最终内容
     */
    private fun freemarkerResolverContent(htmlTemplate: String, templateParamsMap: Map<String, Any>): String {
        val configuration: Configuration = Configuration(Configuration.VERSION_2_3_23)
        val stringLoader: StringTemplateLoader = StringTemplateLoader()
        val templateName = UUID.fastUUID().toString(true)
        stringLoader.putTemplate(templateName, htmlTemplate)
        configuration.setTemplateLoader(stringLoader)
        try {
            val template: Template = configuration.getTemplate(templateName, "utf-8")
            val out: Writer = StringWriter(2048)
            template.process(templateParamsMap, out)
            return out.toString()
        } catch (e: Throwable) {
            log.error("freemarkerResolverContent error: ", e)
        }
        return ""
    }
}
