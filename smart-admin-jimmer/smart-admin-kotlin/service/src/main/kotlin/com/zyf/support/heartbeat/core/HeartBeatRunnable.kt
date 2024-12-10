package com.zyf.support.heartbeat.core

import cn.hutool.core.net.NetUtil
import com.zyf.support.HeartBeatRecord
import org.apache.commons.lang3.StringUtils
import java.lang.management.ManagementFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 心跳线程
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-01-09 20:57:24
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class HeartBeatRunnable(private val recordHandler: IHeartBeatRecordHandler) : Runnable {
    /**
     * 项目路径
     */
    private var curProjectPath: String

    /**
     * 服务器ip（多网卡）
     */
    private var curServerIps: List<String?>

    /**
     * 进程号
     */
    private var curProcessNo: String

    /**
     * 进程开启时间
     */
    private var curProcessStartTime: LocalDateTime

    init {
        val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
        this.curProjectPath = System.getProperty("user.dir")
        this.curServerIps = ArrayList(NetUtil.localIpv4s())
        this.curProcessNo = runtimeMXBean.name.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        this.curProcessStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(runtimeMXBean.startTime), ZoneId.systemDefault())
    }


    override fun run() {
        val heartBeatRecord = HeartBeatRecord {
            this.projectPath = curProjectPath
            this.serverIp = StringUtils.join(curServerIps, ";")
            this.processNo = curProcessNo
            this.processStartTime = curProcessStartTime
            this.heartBeatTime = LocalDateTime.now()
        }
        recordHandler.handler(heartBeatRecord)
    }
}
