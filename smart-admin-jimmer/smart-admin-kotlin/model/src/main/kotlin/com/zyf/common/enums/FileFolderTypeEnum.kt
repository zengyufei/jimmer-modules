package com.zyf.common.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zyf.common.base.BaseEnum
import org.babyfish.jimmer.sql.EnumType

@EnumType(EnumType.Strategy.ORDINAL)
enum class FileFolderTypeEnum(
    @JsonValue override val value: Int,
    val folder: String,
    override val desc: String,
) : BaseEnum {
    /**
     * 通用
     */
    COMMON(1, "public/common/", "通用"),

    /**
     * 公告
     */
    NOTICE(2,  "public/notice/", "公告"),

    /**
     * 帮助中心
     */
    HELP_DOC(3, "public/help-doc/", "帮助中心"),

    /**
     * 意见反馈
     */
    FEEDBACK(4, "public/feedback/", "意见反馈"),
    ;

    companion object {
        /**
         * 公用读取文件夹 public
         */
        const val FOLDER_PUBLIC: String = "public"

        /**
         * 私有读取文件夹 private， 私有文件夹会设置 只读权限，并且 文件url 拥有过期时间
         */
        const val FOLDER_PRIVATE: String = "private"
        @JsonCreator
        fun deserialize(value: Int): FileFolderTypeEnum {
            return when (value) {
                COMMON.value -> COMMON
                NOTICE.value -> NOTICE
                HELP_DOC.value -> HELP_DOC
                FEEDBACK.value -> FEEDBACK
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
