package top.dreamer.demo.api.impl;

import top.dreamer.demo.api.GreetingService;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 16:25
 * @description: 实现类
 */
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name + " !";
    }
}
