package com.zheng.router.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project


class RouterPlugin implements Plugin<Project> {


    //注入插件逻辑到project, 比如动态添加task （task 的参数在 extension添加？） 配置阶段执行
    @Override
    void apply(Project target) {
        println "Plugin，插件代码开始应用到项目 ${target.name}"
       // println "sss"

        target.getExtensions().create("zhouzhengplugin", RouterExtension)

        target.afterEvaluate {
            //配置结束了 project 的配置结束 在这个project 执行换后执行
            RouterExtension extension = target["zhouzhengplugin"]
            println "Plugin，app 的extension${extension.wikiDir}"
            println "Plugin，工程 app 的配置结束"
            target.task('zzCustomTask') {
                doLast {
                    println '执行自定义任务（在r插件中注册）'
                }
            }
        }
    }
}