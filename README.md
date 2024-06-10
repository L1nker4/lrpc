## 架构

TODO：架构图



## TODO

- [ ] 支持服务注册与服务发现，支持：Zookeeper
- [ ] 支持负载均衡策略，包括：轮询、随机、加权轮询、加权随机、一致性哈希
- [ ] 使用Zookeeper作为RPC的注册中心。
- [x] 支持多种序列化方式：JSON、Protobuf、Hessian、Kryo
- [x] 使用Netty作为项目的通信框架，并自定义通信协议，可考虑接口扩展性，支持HTTP等。
- [ ] 采用心跳机制检查Provider的可用性。
- [ ] 提供动态配置的功能。
- [ ] 支持Filter机制，支持自定义Filter



## 通信协议设计

|      字段       | 长度（Byte） |         含义         |
| :-------------: | :----------: | :------------------: |
|  Magic Number   |      4       | 标识协议类型，0x1234 |
|     version     |      1       |       协议版本       |
| Serializer Type |      1       |      序列化方式      |
|  Package Type   |      4       |  包类型，请求或响应  |
|   Data Length   |      4       |       数据长度       |
|   Retain Data   |      2       |       保留字段       |

