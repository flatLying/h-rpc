package top.dreamer.demo.server;


import top.dreamer.demo.api.GreetingService;
import top.dreamer.demo.api.impl.GreetingServiceImpl;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.bootstrap.server_config.ProtocolConfig;
import top.dreamer.service.module.bootstrap.common_config.RegistryConfig;
import top.dreamer.service.module.bootstrap.server_config.ServiceConfig;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 15:37
 * @description: 服务端启动示例
 */
public class ServerDemo {
    public static void main(String[] args) {
        ServiceConfig<GreetingService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(GreetingService.class)
                        .setRef(new GreetingServiceImpl());

        HrpcBootstrap.getInstance()
                .application("Hrpc-Server-Demo")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .protocol(new ProtocolConfig())
//                .service(serviceConfig)
                .scan("top.dreamer.demo.api")
                .start();
    }
}
