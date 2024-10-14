package top.dreamer.service.common.utils;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 21:55
 * @description: 包扫描
 */
public class ClassScanner {
    /**
     * 扫描指定包下的所有类
     * @param packageName 包名
     * @return 类的集合
     */
    public static Set<Class<?>> scanPackage(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))   // 设置要扫描的包
                .setScanners(new SubTypesScanner(false))           // 扫描子类
                .addClassLoaders(ClasspathHelper.contextClassLoader()) // 设置类加载器
        );
        return reflections.getSubTypesOf(Object.class);
    }


    /**
     * 扫描指定包下实现了某接口的所有类
     * @param packageName 包名
     * @param interfaceClass 接口类
     * @param <T> 接口类型
     * @return 类集合
     */
    public static  <T> Set<Class<? extends T>> scanPackage(String packageName, Class<T> interfaceClass) {
        Reflections reflections = new Reflections(packageName);
        // 查找实现了指定接口的所有类
        return reflections.getSubTypesOf(interfaceClass);
    }

    public static void main(String[] args) {
        System.out.println("当前类路径: " + System.getProperty("java.class.path"));
        // 打印类路径以检查是否包含 reflections JAR
        try {
            Class<?> clazz = Class.forName("org.reflections.Reflections");
            System.out.println("Reflections 库已加载: " + clazz);
        } catch (ClassNotFoundException e) {
            System.out.println("Reflections 库未找到，请检查类路径！");
        }
    }
}
