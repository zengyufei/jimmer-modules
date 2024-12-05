package com.zyf.common.utils

import cn.hutool.core.util.StrUtil
import com.zyf.common.annotations.Slf4j
import com.zyf.common.annotations.Slf4j.Companion.log
import com.zyf.common.constant.StringConst
import org.lionsoul.ip2region.xdb.Searcher
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * IP工具类
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/9/14 15:35:11
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright [1024创新实验室](https://1024lab.net)，Since 2012
 */
@Slf4j
object SmartIpUtil {
    private var IP_SEARCHER: Searcher? = null

    /**
     * 初始化数据
     *
     * @param filePath
     */
    @JvmStatic
    fun init(filePath: String?) {
        try {
            val cBuff = Searcher.loadContentFromFile(filePath)
            IP_SEARCHER = Searcher.newWithBuffer(cBuff)
        } catch (e: Throwable) {
            log.error("初始化ip2region.xdb文件失败,报错信息:[{}]", e.message, e)
            throw RuntimeException("系统异常!")
        }
    }


    /**
     * 自定义解析ip地址
     *
     * @param ipStr ipStr
     * @return 返回结果例 [河南省, 洛阳市, 洛龙区]
     */
    fun getRegionList(ipStr: String): List<String> {
        var ipStr = ipStr
        val regionList: MutableList<String> = ArrayList()
        try {
            if (StrUtil.isBlank(ipStr)) {
                return regionList
            }
            ipStr = ipStr.trim { it <= ' ' }
            val region = IP_SEARCHER!!.search(ipStr)
            val split = region.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            regionList.addAll(Arrays.asList(*split))
        } catch (e: Exception) {
            log.error("解析ip地址出错", e)
        }
        return regionList
    }

    /**
     * 自定义解析ip地址
     *
     * @param ipStr ipStr
     * @return 返回结果例 河南省|洛阳市|洛龙区
     */
    fun getRegion(ipStr: String?): String {
        try {
            if (StrUtil.isBlank(ipStr)) {
                return StringConst.EMPTY
            }
            val newIpStr = ipStr?.trim { it <= ' ' }
            return IP_SEARCHER!!.search(newIpStr)
        } catch (e: Exception) {
            log.error("解析ip地址出错", e)
            return StringConst.EMPTY
        }
    }

    val localFirstIp: String?
        /**
         * 获取本机第一个ip
         *
         * @return
         */
        get() {
            val list = localIp
            return if (list.size > 0) list[0] else null
        }

    val localIp: List<String>
        /**
         * 获取本机ip
         *
         * @return
         */
        get() {
            val ipList: MutableList<String> = ArrayList()
            try {
                val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement()
                    val inetAddresses = networkInterface.inetAddresses
                    while (inetAddresses.hasMoreElements()) {
                        val inetAddress = inetAddresses.nextElement()
                        // 排除回环地址和IPv6地址
                        if (!inetAddress.isLoopbackAddress && !inetAddress.hostAddress.contains(StringConst.COLON)) {
                            ipList.add(inetAddress.hostAddress)
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ipList
        }
}
