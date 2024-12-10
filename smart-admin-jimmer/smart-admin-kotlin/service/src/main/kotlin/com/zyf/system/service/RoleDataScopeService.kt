package com.zyf.system.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zyf.common.annotations.Slf4j
import com.zyf.common.code.UserErrorCode
import com.zyf.common.domain.ResponseDTO
import com.zyf.common.enums.DataScopeTypeEnum
import com.zyf.common.enums.DataScopeViewTypeEnum
import com.zyf.service.dto.DataScopeDTO
import com.zyf.service.dto.DataScopeViewTypeVO
import com.zyf.service.dto.RoleDataScopeUpdateForm
import com.zyf.service.dto.RoleDataScopeVO
import com.zyf.system.RoleProps
import com.zyf.system.domain.DataScopeAndViewTypeVO
import com.zyf.system.roleId
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
class RoleDataScopeService(
    val sql: KSqlClient,
    val objectMapper: ObjectMapper,
) {

    /**
     * 获取所有可以进行数据范围配置的信息
     */
    fun dataScopeList(): ResponseDTO<List<DataScopeAndViewTypeVO>> {
        val dataScopeList = getDataScopeType()
        val typeList = getViewType()
        val dataScopeAndTypeList = dataScopeList.map {
            val vo = DataScopeAndViewTypeVO()
            vo.dataScopeType = it.dataScopeType
            vo.dataScopeTypeName = it.dataScopeTypeName
            vo.dataScopeTypeDesc = it.dataScopeTypeDesc
            vo.dataScopeTypeSort = it.dataScopeTypeSort
            vo.viewTypeList = typeList
            vo
        }
        dataScopeAndTypeList.forEach { it.viewTypeList = typeList }
        return ResponseDTO.ok(dataScopeAndTypeList)
    }

    /**
     * 获取某个角色的数据范围设置信息
     */
    fun getRoleDataScopeList(roleId: String): ResponseDTO<List<RoleDataScopeVO>> {
        val roleDataScopeList = sql.findAll(RoleDataScopeVO::class) {
            where(table.roleId eq roleId)
        }
        return ResponseDTO.ok(roleDataScopeList)
    }

    /**
     * 批量设置某个角色的数据范围设置信息
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateRoleDataScopeList(roleDataScopeUpdateForm: RoleDataScopeUpdateForm): ResponseDTO<String?> {
        val batchSetList = roleDataScopeUpdateForm.dataScopeItemList
        if (batchSetList.isEmpty()) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "缺少配置信息")
        }
        sql.save(roleDataScopeUpdateForm) {
            setAssociatedMode(
                RoleProps.ROLE_DATA_SCOPES,
                AssociatedSaveMode.MERGE
            )
        }
        return ResponseDTO.ok()
    }


    /**
     * 获取当前系统存在的数据可见范围
     */
    fun getViewType(): List<DataScopeViewTypeVO> {
        val viewTypeList = mutableListOf<DataScopeViewTypeVO>()
        val enums = DataScopeViewTypeEnum.entries.toTypedArray()

        enums.forEach { viewTypeEnum ->
            val dataScopeViewTypeDTO = DataScopeViewTypeVO(
                viewType = viewTypeEnum.value,
                viewTypeLevel = viewTypeEnum.level,
                viewTypeName = viewTypeEnum.desc
            )
            viewTypeList.add(dataScopeViewTypeDTO)
        }

        return viewTypeList.sortedBy { it.viewTypeLevel }
    }

    fun getDataScopeType(): List<DataScopeDTO> {
        val dataScopeTypeList = mutableListOf<DataScopeDTO>()
        val enums = DataScopeTypeEnum.entries.toTypedArray()

        enums.forEach { typeEnum ->
            val dataScopeDTO = DataScopeDTO(
                dataScopeType = typeEnum.value,
                dataScopeTypeDesc = typeEnum.desc,
                dataScopeTypeName = typeEnum.enumName,
                dataScopeTypeSort = typeEnum.sort
            )
            dataScopeTypeList.add(dataScopeDTO)
        }

        return dataScopeTypeList.sortedBy { it.dataScopeTypeSort }
    }

}