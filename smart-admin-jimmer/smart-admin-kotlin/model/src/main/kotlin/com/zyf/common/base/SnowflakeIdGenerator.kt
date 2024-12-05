package com.zyf.common.base

import com.pukang.init.data.utils.IdWorker
import org.babyfish.jimmer.sql.meta.IdGenerator
import org.babyfish.jimmer.sql.meta.UserIdGenerator

class SnowflakeIdGenerator : UserIdGenerator<String> {
    override fun generate(entityType: Class<*>?): String {
        return IdWorker.nextIdStr
    }
}