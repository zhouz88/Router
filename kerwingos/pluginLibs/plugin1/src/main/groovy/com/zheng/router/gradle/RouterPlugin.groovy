package com.zheng.router.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.google.gson.reflect.TypeToken
import org.gradle.api.Plugin
import org.gradle.api.Project

import groovy.json.JsonSlurper
import org.gradle.internal.impldep.com.amazonaws.internal.FIFOCache
import org.gradle.internal.impldep.com.google.gson.Gson

class RouterPlugin implements Plugin<Project> {


    //注入插件逻辑到project, 比如动态添加task （task 的参数在 extension添加？） 配置阶段执行
    @Override
    void apply(Project target) {
        //注册Transform
        if (target.plugins.hasPlugin(AppPlugin)) {
            //com.android.application子工程
            println "跑Plugin  >>> 注册transform试试"
            AppExtension appExtension = target.extensions.getByType(AppExtension)
            Transform transform = new RouterMappingTransform()
            appExtension.registerTransform(transform)
        }


        //1 自动帮用户传递路径参数到注解处理器中。
        //2 实现构建产物的自动清理 全新的构建都要clean 之前的 (1和2 为插件优化)
        //3 找到javaC 任务 并在javac 完成后 汇总生成好的json文件，最终生成一Md文件
        /**
         * //    kapt {
         //        arguments {
         //            arg("root_project_dir", rootProject.projectDir.getAbsolutePath())
         //        }
         //    }
         */
        if (target.extensions.findByName("kapt") != null) {
            target.extensions.findByName("kapt").arguments {
                 arg("root_project_dir", target.rootProject.projectDir.absolutePath)
            }
        }
        //清除根目录之前的 mapping
        File routerMappingDir1 = new File(target.rootProject.projectDir, "router_mapping")
        if (routerMappingDir1.exists()) {
            routerMappingDir1.deleteDir()
        }
        target.clean.doFirst() {
            println "跑Plugin  >>>>>>>> 清除之前的路由mapping"
            File routerMappingDir = new File(target.rootProject.projectDir, "router_mapping")
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }
        if (!target.plugins.hasPlugin(AppPlugin)) {
            return
        }
        println "跑Plugin  >>>>>>>> 插件代码开始应用到项目 ${target.name}"
       // println "sss"

        Object tttt = target.getExtensions().create("zhouzhengplugin", RouterExtension)

        printf "跑Plugin >>>>>>>> 这里跑了吗" + tttt.getClass().getCanonicalName()
        target.afterEvaluate {
            //配置结束了 project 的配置结束 在这个project 执行换后执行
            RouterExtension extension = target["zhouzhengplugin"]
            println "跑Plugin  >>>>>>>> app 的extension${extension.wikiDir}"
            println "跑Plugin  >>>>>>>> 工程 app 的配置结束"
            target.task('zzCustomTask') {
                doLast {
                    println '执行自定义任务（在r插件中注册）'
                }
            }

            // 在javac compileDebugJavaWithJavac 后生成文档
            target.tasks.findAll {task ->
                task.name.matches("compile.*JavaWithJavac")
            }.each { task ->
                task.doLast {
                    File routerMappingDir = new File(target.rootProject.projectDir, 'router_mapping')
                    if (!routerMappingDir.exists()) {
                        return
                    }
                    File[] allChildFiles = routerMappingDir.listFiles()
                    if (allChildFiles.length < 1) {
                      return
                    }
                    StringBuilder markdownBuilder = new StringBuilder();
                    markdownBuilder.append("# 页面文档\n\n");
                    def gson = new com.google.gson.Gson()
                    allChildFiles.each { child ->
                        if (child.name.endsWith(".json")) {
                            //JsonSlurper jsonSlurper = new JsonSlurper()
                            //def content = jsonSlurper.parse(child)
                            def content = readFileToString(child)
                            def t = gson.fromJson(content, new TypeToken<ArrayList<T>>(){}.type)
                            ((List<T>) t).forEach { ob ->
                                markdownBuilder.append("## $ob.description \n")
                                markdownBuilder.append("- url: $ob.url \n")
                                markdownBuilder.append("- realPath: $ob.realPath\n")
                            }
                        }
                    }
                    RouterExtension extensiont = target["zhouzhengplugin"]
                    File wikiFileDir = new File(extensiont.wikiDir)
                    if (!wikiFileDir.exists()) {
                        wikiFileDir.mkdir()
                    }
                    File wikiFile = new File(wikiFileDir, "页面文档.md")
                    if (wikiFile.exists()) {
                        wikiFile.delete()
                    }
                    wikiFile.write(markdownBuilder.toString())
                }
            }
        }
    }

    class T implements Serializable{
        String description;
        String url;
        String realPath;
    }

   String readFileToString(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }
}