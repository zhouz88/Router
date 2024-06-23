package com.zheng.router.gradle

import java.util.jar.JarEntry
import java.util.jar.JarFile


class RouterMappingCollector {

    private static final String PACKAGE_NAME = "com/zz/kerwingo"
    private static final String CLASS_NAME_PREFIX = "RouteMapping_"
    private static final String CLASS_FILE_SUFFIX = ".class"

    private final Set<String> mappingClassNames = new HashSet<>()


    /**
     * 收集好的映射表类名
     * @return
     */
    Set<String> getMappingClassName() {
        return mappingClassNames
    }

    /**
     * 收集class文件 或者 class文件目录中的映射表类
     * @param classFile
     */
    void collect(File classFile) {
       if (classFile == null || !classFile.exists()) {
           return
       }
        if (classFile.isFile()) {
            if (classFile.absolutePath.contains(PACKAGE_NAME)
             && classFile.name.startsWith(CLASS_NAME_PREFIX)
            && classFile.name.endsWith(CLASS_FILE_SUFFIX)) {
                print "transform file <<<<<<< " + classFile.name
                String className = classFile.name.replace(CLASS_FILE_SUFFIX, "")
                mappingClassNames.add(className)
            }
            return
        }
        classFile.listFiles().each {
            collect(it)
        }
    }

    /**
     * 收集jar包中的目标类
     * @param jarFile
     */
    void collectFromJarFile(File jarFile) {
        Enumeration enumeration = new JarFile(jarFile).entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry)enumeration.nextElement()
            String entryName = jarEntry.getName()
            if (entryName.contains(PACKAGE_NAME) && entryName.contains(CLASS_NAME_PREFIX)
               && entryName.contains(CLASS_FILE_SUFFIX)) {
                print "transform jar <<<<<<< " + entryName
                String className = entryName.replace(PACKAGE_NAME, "")
                .replace("/", "")
                .replace(CLASS_FILE_SUFFIX, "")

                mappingClassNames.add(className)
            }
        }
    }
}