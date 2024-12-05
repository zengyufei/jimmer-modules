package com.zyf.cfg.json

import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.LocalDateTimeUtil
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.zyf.cfg.json.serializer.LongJsonSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@Configuration
class JacksonConfig {

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer? {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.deserializers(
                LocalDateDeserializer(DatePattern.NORM_DATE_FORMAT.dateTimeFormatter)
            )
            builder.deserializers(
                LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMAT.dateTimeFormatter)
            )
            builder.serializers(
                LocalDateSerializer(DatePattern.NORM_DATE_FORMAT.dateTimeFormatter)
            )
            builder.serializers(
                LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMAT.dateTimeFormatter)
            );
            builder.serializerByType(Long::class.java, LongJsonSerializer.INSTANCE);
        }
    }


    /**
     * string 转为 LocalDateTime 配置类
     *
     * @author 卓大
     */
    @Configuration
    class StringToLocalDateTime : Converter<String, LocalDateTime> {

        override fun convert(str: String): LocalDateTime? {
            if (str.isBlank()) {
                return null;
            }
            val localDateTime: LocalDateTime
            try {
                localDateTime = LocalDateTimeUtil.parse(
                    str,
                    DatePattern.NORM_DATETIME_FORMAT.dateTimeFormatter
                )
            } catch (e: DateTimeParseException) {
                throw RuntimeException("请输入正确的日期格式：yyyy-MM-dd HH:mm:ss");
            }
            return localDateTime;
        }
    }


    /**
     * string 转为 LocalDate 配置类
     *
     * @author 卓大
     */
    @Configuration
    class StringToLocalDate : Converter<String, LocalDate> {

        override fun convert(str: String): LocalDate? {
            if (str.isBlank()) {
                return null;
            }
            val localDate: LocalDate
            try {
                localDate = LocalDateTimeUtil.parseDate(
                    str,
                    DatePattern.NORM_DATE_FORMAT.dateTimeFormatter
                )
            } catch (e: DateTimeParseException) {
                throw RuntimeException("请输入正确的日期格式：yyyy-MM-dd");
            }
            return localDate;
        }
    }

}