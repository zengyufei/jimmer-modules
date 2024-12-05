package com.zyf.controller

import com.zyf.support.DictKey
import org.babyfish.jimmer.client.meta.Api
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api("Dict Key Api")
@RestController
@RequestMapping("/dictKey")
class DictKeyController(
    val sqlClient: KSqlClient
) {

    @Api
    @RequestMapping("/list")
    fun list(): List<DictKey> {
        return sqlClient.createQuery(DictKey::class) {
            select(
                table
            )
        }.execute()
    }

}