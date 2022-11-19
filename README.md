## 架构

TODO：架构图



## TODO

- [ ] 提供多种序列化方式：JSON、Protobuf等
- [ ] 提供负载均衡算法，保证Provider的可用性。
- [ ] 使用Zookeeper作为RPC的注册中心。
- [ ] 使用Netty作为项目的通信框架，并自定义通信协议，可考虑接口扩展性，支持HTTP等。
- [ ] 采用心跳机制检查Provider的可用性。
- [ ] 提供动态配置的功能。