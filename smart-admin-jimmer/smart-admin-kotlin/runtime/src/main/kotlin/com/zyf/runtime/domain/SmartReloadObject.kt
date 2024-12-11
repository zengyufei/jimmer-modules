package com.zyf.runtime.domain

import java.lang.reflect.Method

/**
 * Reload 处理程序的实现方法，用于包装以注解 SmartReload 实现的处理类
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2015-03-02 19:11:52
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  [1024创新实验室](https://1024lab.net)
 */
class SmartReloadObject(
    /**
     * 方法对应的实例化对象
     */
    var reloadObject: Any?,
    /**
     * 重新加载执行的方法
     */
    var method: Method?
) {

}
