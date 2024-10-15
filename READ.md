# h-rpc

**h-rpc** 是一个基于 Netty 和 Zookeeper 实现的高性能 RPC 远程调用框架。该项目由我从 0 编写，旨在为开发者提供高效、简洁、可扩展的服务通信能力，并具备服务治理功能，如负载均衡、熔断、限流等。

## 项目特点

- **高性能网络通信**：基于 Netty 框架，使用自定义协议对请求和响应进行封装，提升了网络传输效率。
- **服务治理**：实现了负载均衡、熔断、限流等服务治理功能，增强了系统的稳定性和容错能力。
- **可扩展性强**：支持多种序列化和压缩工具，并通过 SPI+XML 配置和代码配置的形式提升扩展性，便于定制化开发。
- **动态上下限感知和心跳检测**：支持动态上下限感知与心跳检测，提升服务节点的健康监控与动态管理能力。
- **注册中心支持**：允许配置注册中心进行服务发现，默认支持 Zookeeper 作为服务注册中心。

## 功能列表

### 1. 基于 Netty 的网络通信
使用 Netty 作为网络通信框架，实现自定义的 RPC 协议，封装请求和响应的数据结构，提供高效的异步通信能力。

### 2. 负载均衡策略
项目实现了以下几种常见的负载均衡策略：
- 轮询
- 一致性哈希
- 最短响应时间

使用模板方法模式（Template Method Pattern）封装负载均衡的骨架代码，不同策略作为子类进行具体实现。

### 3. 限流器与熔断器
实现了简易版的限流器和熔断器，分别用于保护服务调用方和提供方，增强系统的可用性和稳定性。

### 4. 流量隔离
通过业务分组实现流量隔离，避免辅助业务影响核心业务，确保不同核心业务之间也可以相互隔离，提升系统的安全性和稳定性。

### 5. 多种序列化和压缩工具
使用工厂模式对外提供多种序列化工具和压缩工具，增强代码扩展性。同时通过 SPI + XML 配置和代码配置的形式，让项目的扩展性更强，更加灵活。

### 6. 动态上下限感知与心跳检测
实现动态上下限感知功能，可以监控服务节点的状态变化，自动调整服务上下线。心跳检测用于保证服务节点的健康状态，及时发现和剔除异常节点。

### 7. 注册中心支持
通过配置注册中心进行服务发现与管理，支持动态添加或移除服务节点，默认使用 Zookeeper 作为注册中心。

## 贡献
欢迎提交 issues 和 PR，如果有任何问题或建议，请随时联系我！

## 开源协议
该项目使用 [MIT License](LICENSE) 进行开源。