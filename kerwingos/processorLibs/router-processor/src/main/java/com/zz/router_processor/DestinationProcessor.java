package com.zz.router_processor;

import com.google.auto.service.AutoService;
import com.zz.router_annotation.Destination;

import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)
public class DestinationProcessor extends AbstractProcessor {

    private static final String TAG = "DestinationProcessor";


    /**
     * 编译器找到注解后 执行的方法
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println(TAG + " >>> process start ..."); //输出日志方便调试

        //获取所有标记了destination注解类的信息
        Set<Element> allDestinationElements = (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Destination.class);

        System.out.println(TAG + " >>> all destination" + allDestinationElements.size());

        if (allDestinationElements.size() < 1) {
            return false;
        }
        //将要自动生成类的类名
        // 编译期间 hook 一些代码。
        String className = "RouteMapping_" + System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        builder.append("package com.zz.kerwingo;\n");
        builder.append("import java.util.HashMap;\n")
                .append("import java.util.Map;\n")
                .append("public class ").append(className).append(" {\n\n")
                .append("    public static Map<String, String> get() {\n")
                .append("        Map<String, String> mapping = new HashMap<>();\n");

        //遍历所有注解信息，挨个获取详细信息
        for (Element element : allDestinationElements) {
            final TypeElement typeElement = (TypeElement) element;
            final Destination destination = typeElement.getAnnotation(Destination.class);

            if (destination == null) {
                continue;
            }
            final String url = destination.url();
            final String description = destination.description();
            final String realPath = typeElement.getQualifiedName().toString();
            System.out.println(TAG + "  <<< URL = " + url);
            System.out.println(TAG + "  <<< description = " + description);
            System.out.println(TAG + "  <<< realPath = " + realPath);

            builder.append("mapping.put(")
                    .append("\"" + url + "\"")
                    .append(", ")
                    .append("\"" + realPath + "\"")
                    .append(");\n");
        }

        builder.append("return mapping;\n");
        builder.append("}\n");
        builder.append("}\n");

        try {
            String mappingFullClassName = "com.zz.kerwingo."+className;
            JavaFileObject source = processingEnv.getFiler().createSourceFile(mappingFullClassName);
            Writer writer = source.openWriter();
            System.out.println(TAG + "  <<< mapping = " + builder.toString());
            writer.write(builder.toString());
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("STH wrong!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return false;
    }

    /**
     * 告诉编译器当前 处理器支持的注解类型
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Destination.class.getCanonicalName());
    }
    /**
     *    public static void main(String[] args) {
     *         System.out.println(OuterClass.InnerClass.class.getCanonicalName()); // 输出 "com.example.OuterClass.InnerClass"
     *         System.out.println(OuterClass.InnerClass.class.getName()); // 输出 "com.example.OuterClass$InnerClass"
     *     }
     *     在这个例子中，getCanonicalName() 返回了不包含美元符号的规范名称，而 getName() 返回了包含美元符号的名称。
     */
}
