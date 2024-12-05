package com.zyf.support.service

import cn.hutool.crypto.symmetric.SM4
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.constant.StringConst
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.stereotype.Service
import java.security.Security
import java.util.*

/**
 * 国产 SM4 加密 和 解密
 * 1、国密SM4 要求秘钥为 128bit，转化字节为 16个字节；
 * 2、js前端使用 UCS-2 或者 UTF-16 编码，字母、数字、特殊符号等 占用1个字节；
 * 3、java中 每个 字母数字 也是占用1个字节；
 * 4、所以：前端和后端的 秘钥Key 组成为：字母、数字、特殊符号 一共16个即可
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/21 11:41:46
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
@Service
class ApiEncryptServiceSmImpl : ApiEncryptService {
    override fun encrypt(data: String?): String {
        try {
            // 第一步： SM4 加密

            val sm4 = SM4(hexToBytes(stringToHex(SM4_KEY)))
            val encryptHex = sm4.encryptHex(data)

            // 第二步： Base64 编码
            return String(Base64.getEncoder().encode(encryptHex.toByteArray(charset(CHARSET))), charset(CHARSET))
        } catch (e: Exception) {
            log.error(e.message, e)
            return StringConst.EMPTY
        }
    }


    override fun decrypt(data: String?): String {
        try {
            // 第一步： Base64 解码

            val base64Decode = Base64.getDecoder().decode(data)

            // 第二步： SM4 解密
            val sm4 = SM4(hexToBytes(stringToHex(SM4_KEY)))
            return sm4.decryptStr(String(base64Decode))
        } catch (e: Exception) {
            log.error(e.message, e)
            return StringConst.EMPTY
        }
    }


    companion object {
        private const val CHARSET = "UTF-8"
        private const val SM4_KEY = "1024lab__1024lab"

        init {
            Security.addProvider(BouncyCastleProvider())
        }


        fun stringToHex(input: String): String {
            val chars = input.toCharArray()
            val hex = StringBuilder()
            for (c in chars) {
                hex.append(Integer.toHexString(c.code))
            }
            return hex.toString()
        }


        /**
         * 16 进制串转字节数组
         *
         * @param hex 16进制字符串
         * @return byte数组
         */
        fun hexToBytes(hex: String): ByteArray {
            var hex = hex
            var length = hex.length
            val result: ByteArray
            if (length % 2 == 1) {
                length++
                result = ByteArray((length / 2))
                hex = "0$hex"
            } else {
                result = ByteArray((length / 2))
            }
            var j = 0
            var i = 0
            while (i < length) {
                result[j] = hexToByte(hex.substring(i, i + 2))
                j++
                i += 2
            }
            return result
        }

        /**
         * 16 进制字符转字节
         *
         * @param hex 16进制字符 0x00到0xFF
         * @return byte
         */
        private fun hexToByte(hex: String): Byte {
            return hex.toInt(16).toByte()
        }
    }
}
