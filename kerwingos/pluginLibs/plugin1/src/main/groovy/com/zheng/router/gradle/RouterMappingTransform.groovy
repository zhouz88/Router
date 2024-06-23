package com.zheng.router.gradle

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class RouterMappingTransform extends Transform {
    /**
     * 当前Transform的名称，会打印在gradle的日志里面
     * @return
     */
    @Override
    String getName() {
        return "RouterMappingTransform"
    }

    /**
     *  返回告知编译器，当前Transform需要消费的输入类型
     *  这里是CLASS类型
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 告诉编译器 transform 作用范围 是整个工程还是 当前小小子工程
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 告诉编译 是否支持增量编译
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 当编译器收集所有的class后，会被打包一个transformInvocation,通过这个方法回调，可以对class做二次处理
     * @param transformInvocation the invocation object containing the transform inputs.
     * @throws TransformException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //1. 遍历所有的Input
        //2. 对Input进行二次处理
        //3. 将Input 拷贝到目标目录

        RouterMappingCollector collector = new RouterMappingCollector()
        transformInvocation.inputs.each {
            //把文件夹类型的目录拷贝到目标目录
            it.directoryInputs.each { dirInput ->
                def destDir =  transformInvocation
                        .outputProvider.getContentLocation(
                        dirInput.name, dirInput.contentTypes,
                        dirInput.scopes, Format.DIRECTORY)
                collector.collect(dirInput.file)
                FileUtils.copyDirectory(dirInput.file, destDir)
            }

            //对jar类型的输入拷贝到目标目录
            it.jarInputs.each { jarInput ->
                def dest = transformInvocation.outputProvider
                   .getContentLocation(jarInput.name,
                           jarInput.contentTypes, jarInput.scopes, Format.JAR)
                collector.collectFromJarFile(jarInput.file)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        println ("${getName()} all mapping class name = " + collector.mappingClassName)

        File mappingJarFile = transformInvocation.outputProvider.getContentLocation("router_mapping", getOutputTypes(), getScopes(),
        Format.JAR) //得到了生成jar包的位置

        println("拿到jar报位置" + mappingJarFile.absolutePath)
        if (mappingJarFile.getParentFile().exists()) {
            mappingJarFile.getParentFile().mkdir()
        }

        if (mappingJarFile.exists()) {
            mappingJarFile.delete()
        }

        FileOutputStream outputStream = new FileOutputStream(mappingJarFile)
        JarOutputStream joutputStream = new JarOutputStream(outputStream)
        ZipEntry zipEntry = new ZipEntry(RouterMappingByteCodeBuilder.CLASS_NAME + ".class")

        joutputStream.putNextEntry(zipEntry)
        joutputStream.write(RouterMappingByteCodeBuilder.get(collector.mappingClassName))

        joutputStream.closeEntry()
        joutputStream.close()
        outputStream.close()
    }
}
