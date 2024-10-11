package top.dreamer.demo.client;

import lombok.extern.slf4j.Slf4j;
import top.dreamer.demo.api.GreetingService;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.bootstrap.client_config.ReferenceConfig;
import top.dreamer.service.module.bootstrap.common_config.RegistryConfig;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HSerializeType;


/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 15:37
 * @description: 客户端调用示例
 */
@Slf4j
public class ClientDemo {
    public static void main(String[] args) {
        ReferenceConfig<GreetingService> reference = new ReferenceConfig<>();
        reference.setInterface(GreetingService.class);
        reference.setCompressType(HCompressType.GZIP);
        reference.setSerializeType(HSerializeType.FAST_JSON);

        HrpcBootstrap.getInstance()
                .application("Hrpc-Client-Demo")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(reference)
                .connect();

        GreetingService greetingService = reference.get();
        String response = greetingService.sayHello("Hello World");
        log.info("RPC请求响应为：【{}】", response);
    }
}
