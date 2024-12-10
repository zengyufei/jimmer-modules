package com.zyf.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.enums.SerialNumberIdEnum
import com.zyf.common.enums.SerialNumberRuleTypeEnum
import com.zyf.common.exception.BusinessException
import com.zyf.common.utils.SmartEnumUtil
import com.zyf.repository.support.SerialNumberRecordRepository
import com.zyf.repository.support.SerialNumberRepository
import com.zyf.support.*
import com.zyf.support.domain.SerialNumberGenerateResultBO
import com.zyf.support.domain.SerialNumberInfoBO
import com.zyf.support.domain.SerialNumberLastGenerateBO
import jakarta.annotation.PostConstruct
import org.apache.commons.lang3.RandomUtils
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.plus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 单据序列号 基类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2022-03-25 21:46:07
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
abstract class SerialNumberBaseService(
    open val sql: KSqlClient,
    open val objectMapper: ObjectMapper,
    open val serialNumberRepository: SerialNumberRepository,
    open val serialNumberRecordRepository: SerialNumberRecordRepository,
) : ISerialNumberService {


    private val serialNumberMap = ConcurrentHashMap<String, SerialNumberInfoBO>()

    abstract fun generateSerialNumberList(serialNumber: SerialNumberInfoBO, count: Int): List<String>

    @PostConstruct
    fun init() {
        val serialNumberList: List<SerialNumber> = serialNumberRepository.listAll()
        if (serialNumberList.isEmpty()) {
            return
        }
        for (serialNumber in serialNumberList) {
            val ruleTypeEnum: SerialNumberRuleTypeEnum = SmartEnumUtil.getEnumByName<SerialNumberRuleTypeEnum>(serialNumber.ruleType.uppercase(Locale.getDefault())) ?: throw ExceptionInInitializerError("cannot find rule type , id : " + serialNumber.serialNumberId)

            val format = serialNumber.format!!
            val startIndex = format.indexOf("[n")
            val endIndex = format.indexOf("n]")
            if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
                throw ExceptionInInitializerError("[nnn] 配置错误，请仔细查看 id : " + serialNumber.serialNumberId)
            }

            val numberFormat = format.substring(startIndex + 1, endIndex + 1)

            if (serialNumber.stepRandomRange < 1) {
                throw ExceptionInInitializerError("random step range must greater than 1 " + serialNumber.serialNumberId)
            }

            val serialNumberInfoBO = SerialNumberInfoBO()
            serialNumberInfoBO.serialNumberId = serialNumber.serialNumberId
            serialNumberInfoBO.serialNumberRuleTypeEnum = ruleTypeEnum
            serialNumberInfoBO.initNumber = serialNumber.initNumber
            serialNumberInfoBO.format = serialNumber.format
            serialNumberInfoBO.stepRandomRange = serialNumber.stepRandomRange
            serialNumberInfoBO.haveDayFlag = format.contains(SerialNumberRuleTypeEnum.DAY.value)
            serialNumberInfoBO.haveMonthFlag = format.contains(SerialNumberRuleTypeEnum.MONTH.value)
            serialNumberInfoBO.haveYearFlag = format.contains(SerialNumberRuleTypeEnum.YEAR.value)
            serialNumberInfoBO.numberCount = endIndex - startIndex
            serialNumberInfoBO.numberFormat = "\\[$numberFormat\\]"

            serialNumberMap[serialNumber.serialNumberId] = serialNumberInfoBO
        }

        // 初始化数据
        initLastGenerateData(serialNumberList)
    }

    /**
     * 初始化上次生成的数据
     *
     * @param serialNumberList
     */
    abstract fun initLastGenerateData(serialNumberList: List<SerialNumber>)

    override fun generate(serialNumberIdEnum: SerialNumberIdEnum): String? {
        val generateList = this.generate(serialNumberIdEnum, 1)
        if (generateList.isEmpty()) {
            throw BusinessException("cannot generate : $serialNumberIdEnum")
        }
        return generateList[0]
    }

    override fun generate(serialNumberIdEnum: SerialNumberIdEnum, count: Int): List<String> {
        val serialNumberInfoBO = serialNumberMap[serialNumberIdEnum.value] ?: throw BusinessException("cannot found SerialNumberId : $serialNumberIdEnum")
        return this.generateSerialNumberList(serialNumberInfoBO, count)
    }

    /**
     * 循环生成 number 集合
     *
     * @param lastGenerate
     * @param serialNumberInfo
     * @param count
     * @return
     */
    protected fun loopNumberList(lastGenerate: SerialNumberLastGenerateBO, serialNumberInfo: SerialNumberInfoBO, count: Int): SerialNumberGenerateResultBO {
        var lastNumber = lastGenerate.lastNumber
        var isReset = false
        if (isResetInitNumber(lastGenerate, serialNumberInfo)) {
            lastNumber = serialNumberInfo.initNumber
            isReset = true
        }

        val numberList = ArrayList<Long>(count)
        for (i in 0 until count) {
            val stepRandomRange = serialNumberInfo.stepRandomRange
            lastNumber = if (stepRandomRange!! > 1) {
                lastNumber!! + RandomUtils.nextInt(1, stepRandomRange + 1)
            } else {
                lastNumber!! + 1
            }

            numberList.add(lastNumber)
        }

        val bo = SerialNumberGenerateResultBO()
        bo.serialNumberId = serialNumberInfo.serialNumberId
        bo.lastNumber = lastNumber
        bo.lastTime = LocalDateTime.now()
        bo.numberList = numberList
        bo.isReset = isReset
        return bo
    }

    protected fun saveRecord(resultBO: SerialNumberGenerateResultBO) {
        val effectRows = sql.createUpdate(SerialNumberRecord::class) {
            set(table.lastNumber, resultBO.lastNumber!!)
            set(table.updateCount, table.updateCount + 1)
            where(table.serialNumberId eq resultBO.serialNumberId)
            where(table.recordDate eq resultBO.lastTime!!.toLocalDate())
        }.execute()


        // 需要插入
        if (effectRows == 0) {
            val record = SerialNumberRecord {
                this.serialNumberId = resultBO.serialNumberId!!
                this.recordDate = LocalDate.now()
                this.lastTime = resultBO.lastTime!!
                this.lastNumber = resultBO.lastNumber!!
                this.updateCount = resultBO.numberList!!.size.toLong()
            }
            serialNumberRecordRepository.insert(record)
        }
    }

    /**
     * 若不在规则周期内，重制初始值
     *
     * @return
     */
    private fun isResetInitNumber(lastGenerate: SerialNumberLastGenerateBO, serialNumberInfo: SerialNumberInfoBO): Boolean {
        val lastTime = lastGenerate.lastTime ?: return true

        val serialNumberRuleTypeEnum = serialNumberInfo.serialNumberRuleTypeEnum
        val lastTimeYear = lastTime.year
        val lastTimeMonth = lastTime.monthValue
        val lastTimeDay = lastTime.dayOfYear

        val now = LocalDateTime.now()

        return when (serialNumberRuleTypeEnum) {
            SerialNumberRuleTypeEnum.YEAR -> lastTimeYear != now.year
            SerialNumberRuleTypeEnum.MONTH -> lastTimeYear != now.year || lastTimeMonth != now.monthValue
            SerialNumberRuleTypeEnum.DAY -> lastTimeYear != now.year || lastTimeDay != now.dayOfYear
            else -> false
        }
    }

    /**
     * 替换特殊rule，即替换[yyyy][mm][dd][nnn]等规则
     */
    protected fun formatNumberList(generateResult: SerialNumberGenerateResultBO, serialNumberInfo: SerialNumberInfoBO): List<String> {
        /**
         * 第一步：替换年、月、日
         */

        val lastTime = generateResult.lastTime!!.toLocalDate()
        val year = lastTime.year.toString()
        val month = if (lastTime.monthValue > 9) lastTime.monthValue.toString() else "0" + lastTime.monthValue
        val day = if (lastTime.dayOfMonth > 9) lastTime.dayOfMonth.toString() else "0" + lastTime.dayOfMonth

        // 把年月日替换
        var format = serialNumberInfo.format

        if (serialNumberInfo.haveYearFlag!!) {
            format = format!!.replace(SerialNumberRuleTypeEnum.YEAR.regex.toRegex(), year)
        }
        if (serialNumberInfo.haveMonthFlag!!) {
            format = format!!.replace(SerialNumberRuleTypeEnum.MONTH.regex.toRegex(), month)
        }
        if (serialNumberInfo.haveDayFlag!!) {
            format = format!!.replace(SerialNumberRuleTypeEnum.DAY.regex.toRegex(), day)
        }


        /**
         * 第二步：替换数字
         */
        val numberList: MutableList<String> = ArrayList(generateResult.numberList!!.size)
        for (number in generateResult.numberList!!) {
            val numberStringBuilder = StringBuilder()
            val currentNumberCount = number.toString().length
            // 数量不够，前面补0
            if (serialNumberInfo.numberCount!! > currentNumberCount) {
                val remain = serialNumberInfo.numberCount!! - currentNumberCount
                for (i in 0 until remain) {
                    numberStringBuilder.append(0)
                }
            }
            numberStringBuilder.append(number)
            // 最终替换
            val finalNumber = format!!.replace(serialNumberInfo.numberFormat!!.toRegex(), numberStringBuilder.toString())
            numberList.add(finalNumber)
        }
        return numberList
    }
}
