package com.zyf.support.service

import cn.hutool.crypto.symmetric.AES
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.constant.StringConst
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.util.*

/**
 * AES 加密和解密
 * 1、AES加密算法支持三种密钥长度：128位、192位和256位，这里选择128位
 * 2、AES 要求秘钥为 128bit，转化字节为 16个字节；
 * 3、js前端使用 UCS-2 或者 UTF-16 编码，字母、数字、特殊符号等 占用1个字节；
 * 4、所以：秘钥Key 组成为：字母、数字、特殊符号 一共16个即可
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/21 11:41:46
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
@Slf4j
class ApiEncryptServiceAesImpl : ApiEncryptService {
    override fun encrypt(data: String?): String {
        try {
            //  AES 加密 并转为 base64
            val aes = AES(hexToBytes(stringToHex(AES_KEY)))
            return aes.encryptBase64(data)
        } catch (e: Exception) {
            log.error(e.message, e)
            return StringConst.EMPTY
        }
    }

    override fun decrypt(data: String?): String {
        try {
            // 第一步： Base64 解码
            val base64Decode = Base64.getDecoder().decode(data)

            // 第二步： AES 解密
            val aes = AES(hexToBytes(stringToHex(AES_KEY)))
            val decryptedBytes = aes.decrypt(base64Decode)
            return String(decryptedBytes, charset(CHARSET))
        } catch (e: Exception) {
            log.error(e.message, e)
            return StringConst.EMPTY
        }
    }

    companion object {
        private const val CHARSET = "UTF-8"

        private const val AES_KEY = "1024lab__1024lab"

        init {
            Security.addProvider(BouncyCastleProvider())
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

        fun stringToHex(input: String): String {
            val chars = input.toCharArray()
            val hex = StringBuilder()
            for (c in chars) {
                hex.append(Integer.toHexString(c.code))
            }
            return hex.toString()
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
