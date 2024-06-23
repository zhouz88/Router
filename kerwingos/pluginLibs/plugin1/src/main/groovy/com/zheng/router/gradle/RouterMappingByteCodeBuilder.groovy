package com.zheng.router.gradle

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class RouterMappingByteCodeBuilder implements Opcodes{

    public static final CLASS_NAME = "com/zz/kerwingo/RouterMapping"

    //生成字节码文件
    static byte[] get(Set<String> allMappingNames) {
        //1 创建类
        //2 创建构造方法
        //3 创建get 方法
        // 创建类，塞入所有映射表内容
        // 生成某个java 文件字节码的插件
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS) //用来修改或者生成一个类的接口

        cw.visit(Opcodes.V1_8, ACC_PUBLIC + ACC_SUPER, CLASS_NAME,
                null, "java/lang/Object", null)

        MethodVisitor mv
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V",
                null, null)

        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/Object", "<init>", "()V", false
        ) //用来调用某个类的方法
        mv.visitInsn(Opcodes.RETURN) //调用没有参数的字节码指令
        mv.visitMaxs(1, 1)//局部变量的栈帧大小
        mv.visitEnd()

        //创建get方法
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "get", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", null);
        mv.visitCode()
        //visitCode end 之间填写指令
        mv.visitTypeInsn(NEW, "java/util/HashMap")
        mv.visitInsn(DUP)  //入栈
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap","<init>","()V",
        false) //这里得到了一个HASHMAP的instance
        mv.visitVarInsn(ASTORE, 0) //保存
        //塞入所有其他内容
        allMappingNames.each {
            mv.visitVarInsn(ALOAD, 0)
            mv.visitMethodInsn(INVOKESTATIC, "com/zz/kerwingo/$it",
                    "get", "()Ljava/util/Map;", false)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map",
                    "putAll", "(Ljava/util/Map;)V", true);
        }
        //返回这个map
        mv.visitVarInsn(ALOAD, 0)
        mv.visitInsn(ARETURN)
        mv.visitMaxs(2,2)
        mv.visitEnd()
        return cw.toByteArray()
    }
}