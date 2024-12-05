package com.pukang.init.data.utils

import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * seata改良版雪花算法idWorker
 *
 * @author zyf
 * @date 2024/04/18
 */
class IdWorker private constructor(workerId: Long?) {
    /**
     * Start time cut (2020-05-03)
     */
    private val twepoch = 1588435200000L

    /**
     * The number of bits occupied by workerId
     */
    private val workerIdBits = 10

    /**
     * The number of bits occupied by timestamp
     */
    private val timestampBits = 41

    /**
     * The number of bits occupied by sequence
     */
    private val sequenceBits = 12

    /**
     * Maximum supported machine id, the result is 1023
     */
    private val maxWorkerId = (-1 shl workerIdBits).inv()

    /**
     * business meaning: machine ID (0 ~ 1023)
     * actual layout in memory:
     * highest 1 bit: 0
     * middle 10 bit: workerId
     * lowest 53 bit: all 0
     */
    private var workerId: Long = 0

    /**
     * timestamp and sequence mix in one Long
     * highest 11 bit: not used
     * middle  41 bit: timestamp
     * lowest  12 bit: sequence
     */
    private var timestampAndSequence: AtomicLong? = null

    /**
     * mask that help to extract timestamp and sequence from a long
     */
    private val timestampAndSequenceMask = (-1L shl (timestampBits + sequenceBits)).inv()


    private class IdWorkerHolder {
        val instance: IdWorker = IdWorker(null)
    }

    /**
     * instantiate an IdWorker using given workerId
     *
     * @param workerId if null, then will auto assign one
     */
    init {
        initTimestampAndSequence()
        initWorkerId(workerId)
    }

    /**
     * init first timestamp and sequence immediately
     */
    private fun initTimestampAndSequence() {
        val timestamp = newestTimestamp
        val timestampWithSequence = timestamp shl sequenceBits
        this.timestampAndSequence = AtomicLong(timestampWithSequence)
    }

    /**
     * init workerId
     *
     * @param workerId if null, then auto generate one
     */
    private fun initWorkerId(workerId: Long?) {
        var workerId = workerId
        if (workerId == null) {
            workerId = generateWorkerId()
        }
        if (workerId > maxWorkerId || workerId < 0) {
            val message = String.format("worker Id can't be greater than %d or less than 0", maxWorkerId)
            throw IllegalArgumentException(message)
        }
        this.workerId = workerId shl (timestampBits + sequenceBits)
    }

    /**
     * get next UUID(base on snowflake algorithm), which look like:
     * highest 1 bit: always 0
     * next   10 bit: workerId
     * next   41 bit: timestamp
     * lowest 12 bit: sequence
     *
     * @return UUID
     */
    private fun nextId(): Long {
        waitIfNecessary()
        val next = timestampAndSequence!!.incrementAndGet()
        val timestampWithSequence = next and timestampAndSequenceMask
        return workerId or timestampWithSequence
    }

    /**
     * block current thread if the QPS of acquiring UUID is too high
     * that current sequence space is exhausted
     */
    private fun waitIfNecessary() {
        val currentWithSequence = timestampAndSequence!!.get()
        val current = currentWithSequence ushr sequenceBits
        val newest = newestTimestamp
        if (current >= newest) {
            try {
                Thread.sleep(5)
            } catch (ignore: InterruptedException) {
                // don't care
            }
        }
    }

    private val newestTimestamp: Long
        /**
         * get newest timestamp relative to twepoch
         */
        get() = System.currentTimeMillis() - twepoch

    /**
     * auto generate workerId, try using mac first, if failed, then randomly generate one
     *
     * @return workerId
     */
    private fun generateWorkerId(): Long {
        return try {
            generateWorkerIdBaseOnMac()
        } catch (e: Exception) {
            generateRandomWorkerId()
        }
    }

    /**
     * use lowest 10 bit of available MAC as workerId
     *
     * @return workerId
     * @throws Exception when there is no available mac found
     */
    @Throws(Exception::class)
    private fun generateWorkerIdBaseOnMac(): Long {
        val all = NetworkInterface.getNetworkInterfaces()
        while (all.hasMoreElements()) {
            val networkInterface = all.nextElement()
            val isLoopback = networkInterface.isLoopback
            val isVirtual = networkInterface.isVirtual
            if (isLoopback || isVirtual) {
                continue
            }
            val mac = networkInterface.hardwareAddress
            return ((mac[4].toInt() and 3) shl 8 or (mac[5].toInt() and 0xFF)).toLong()
        }
        throw RuntimeException("no available mac found")
    }

    /**
     * randomly generate one as workerId
     *
     * @return workerId
     */
    private fun generateRandomWorkerId(): Long {
        return Random().nextInt(maxWorkerId + 1).toLong()
    }

    companion object {
        private val instance = IdWorkerHolder().instance

        val nextIdStr: String
            /**
             * get next UUID(base on snowflake algorithm), which look like:
             * highest 1 bit: always 0
             * next   10 bit: workerId
             * next   41 bit: timestamp
             * lowest 12 bit: sequence
             *
             * @return UUID
             */
            get() = instance.nextId().toString()

        val nextId: Long
            /**
             * get next UUID(base on snowflake algorithm), which look like:
             * highest 1 bit: always 0
             * next   10 bit: workerId
             * next   41 bit: timestamp
             * lowest 12 bit: sequence
             *
             * @return UUID
             */
            get() = instance.nextId()
    }
}