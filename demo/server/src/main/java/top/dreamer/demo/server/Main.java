package top.dreamer.demo.server;

import top.dreamer.service.common.config.HRpcDefaultConfiguration;

import java.util.Set;

import static top.dreamer.service.common.utils.ClassScanner.scanPackage;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 22:22
 * @description: 测试
 */
public class Main {
    public static void main(String[] args) {
        HRpcDefaultConfiguration hRpcDefaultConfiguration = new HRpcDefaultConfiguration();
        System.out.println(hRpcDefaultConfiguration.getBalancer());
    }
}
