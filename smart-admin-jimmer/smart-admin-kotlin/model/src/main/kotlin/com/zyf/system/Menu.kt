package com.zyf.system

import com.zyf.common.base.BaseEntity
import com.zyf.common.base.SnowflakeIdGenerator
import com.zyf.common.base.TenantAware
import org.babyfish.jimmer.sql.*

@Entity
@Table(name = "t_menu")
interface Menu : TenantAware, BaseEntity {

    /** 菜单id */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    @Column(name = "menu_id")
    val menuId: String

    /** 菜单名称 */
    @Key
    @Column(name = "menu_name")
    val menuName: String

    /** 类型 */
    @Column(name = "menu_type")
    val menuType: Int

    /** 父级菜单id */
    @IdView("parent")
    val parentId: String?

    /** 显示顺序 */
    @Column(name = "sort")
    val sort: Int?

    /** 路由地址 */
    @Column(name = "path")
    val path: String?

    /** 组件路径 */
    @Column(name = "component")
    val component: String?

    /** 权限类型 */
    @Column(name = "perms_type")
    val permsType: Int?

    /** 后端权限字符串 */
    @Column(name = "api_perms")
    val apiPerms: String?

    /** 前端权限字符串 */
    @Column(name = "web_perms")
    val webPerms: String?

    /** 菜单图标 */
    @Column(name = "icon")
    val icon: String?

    /** 功能点关联菜单ID */
    @Column(name = "context_menu_id")
    val contextMenuId: String?

    /** 是否为外链 */
    @Column(name = "frame_flag")
    val frameFlag: Boolean

    /** 外链地址 */
    @Column(name = "frame_url")
    val frameUrl: String?

    /** 是否缓存 */
    @Column(name = "cache_flag")
    val cacheFlag: Boolean

    /** 显示状态 */
    @Column(name = "visible_flag")
    val visibleFlag: Boolean

    /** 禁用状态 */
    @Column(name = "disabled_flag")
    val disabledFlag: Boolean

    @Key
    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: Menu?

    @OneToMany(mappedBy = "parent")
    val children: List<Menu>

    @IdView("roles")
    val roleIds: List<String>

    @ManyToMany(mappedBy = "menus")
    val roles: List<Role>

}