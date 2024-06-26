[![](https://img.shields.io/badge/release-v1.0_istio-lightgray.svg)](https://github.com/ReionChan/istio-servicemesh-microservice-arch)&nbsp;[![](https://img.shields.io/badge/license-Apache--2.0-orange.svg)](https://github.com/ReionChan/istio-servicemesh-microservice-arch/blob/main/LICENSE)&nbsp;[![](https://img.shields.io/badge/Author-ReionChan-cyan.svg)](https://reionchan.github.io/)

# 基于『Spring Boot + Istio』云原生️的服务网格骨架

> **子曰：欣聞關注，樂見散佈！**&emsp;&emsp; <a href="https://github.com/ReionChan/istio-servicemesh-microservice-arch/stargazers"><img src="https://img.shields.io/github/stars/ReionChan/istio-servicemesh-microservice-arch?style=social&label=Star" title="关注" alt="关注" height="18" /></a>&emsp;<a href="https://github.com/ReionChan/istio-servicemesh-microservice-arch/network/members"><img src="https://img.shields.io/github/stars/ReionChan/istio-servicemesh-microservice-arch?style=social&label=Fork" title="关注" alt="关注" height="18" /></a>

## 项目架构

![](https://raw.githubusercontent.com/ReionChan/PhotoRepo/master/arch/istio-servicemesh-arch.png)

&emsp;&emsp;在引入 Istio 支持后，将 Spring Cloud Kubernetes 架构中遗留的编程式的 API 网关、熔断降级、认证授权完全替换成基于云原生的基础设施替换，你甚至会发现目前的微服务已经完全与 Spring Cloud 框架脱钩，转而只使用 Spring Boot 依赖。

&emsp;&emsp;项目中有关服务间的 RPC 调用已从 Spring Cloud OpenFeign 剥离，改用存粹的 Open Feign 依赖。之前依托 Spring Security OAuth2 的客户端模式实现服务间零信任网络，也被采取 ***SideCar 边车模式*** 的 mTLS 的安全网络层所替代。而对外部请求的认证权限控制已完全交给 Istio Security API 做声明式配置控制，在代码层级可以发现之前基于 Spring Security 的角色权限声明式注解已完全被注释。之所以目前项目还依赖 Spring Securiy，仅仅是因为本项目使用了 Spring Securiy OAuth2 实现，借由后者的 ***password 模式*** 实现己方登录获取 **JWT 令牌**，而至于请求对 **JWT 令牌**的校验也已交给 Istio 处理，Spring Security OAuth2 Server 仅仅提供 **JWKS** 支持。Istio 的整体安全架构参考这个摘自官方的图：

![](https://istio.io/latest/zh/docs/concepts/security/arch-sec.svg)

&emsp;&emsp;值得注意的是，`arch-iam` 模块的密钥库 `arch_keystore.jks` 中的密钥由 Istio 的 **Root CA** 为的中间证书 **Intermediate CA** 制作产生，然后导出公钥 `public.pem` 给 OAuth2 Resource 端，这样由 Spring Security OAuth2 Server 生成的 JWT 令牌才能被 Istios 的 **JWT Rule** 校验通过，详细参考 [证书管理-插入 CA 证书](https://istio.io/latest/zh/docs/tasks/security/cert-management/plugin-ca-cert/)。本样例为实验环境，故采取该文档中的自签名证书制作流程：

* **Root CA** 对应项目目录 `certs/root-*` 文件
* **Intermediate CA** 对应项目目录 `certs/minikube/ca-*` 文件

![](https://istio.io/latest/zh/docs/tasks/security/cert-management/plugin-ca-cert/ca-hierarchy.svg)

## 项目运行

### Istio sidecar 模式

> 🔔 容器内 minikube kubernetes 环境，执行以下命令使 Istio Kubernetes Gateway 获得集群外本机 IP 及端口映射：
> 		`minikube tunnel `
```sh
# 应用 all in one 部署资源描述
kubectl apply -f https://raw.githubusercontent.com/ReionChan/istio-servicemesh-microservice-arch/main/arch-istio-all-in-one.yaml
```

### Istio non-sidecar 模式

> 🔔 容器内 minikube kubernetes 环境，执行以下命令使 Istio Kubernetes Gateway 获得集群外本机 IP 及端口映射：
> 		`minikube tunnel `

```sh
# 应用 all in one 部署资源描述
kubectl apply -f https://raw.githubusercontent.com/ReionChan/istio-servicemesh-microservice-arch/main/arch-istio-dev-all-in-one.yaml
```

### dev 模式

```sh
# 采用 skaffold 部署到本地 Docker 容器内的 minikube 环境
# 根据最后输出的本地转发端口进行接口访问
skaffold dev --tag 1.0_k8s --tail=true --no-prune=false --cache-artifacts=false
```


## Web API 端点

* 应用内部零信任网络端点认证端点 *OAuth2 Client - credentials 模式*（包含：后台服务、前台 Web 端服务、前端 App 端）

  ```sh
  # 示例演示 WEB 前端认证获得访问令牌
  POST http://localhost:9000/arch-iam/oauth2/token
  Content-Type: application/x-www-form-urlencoded
  Authorization: Basic YXJjaC13ZWI6c2VjcmV0d2Vi
  
  grant_type=client_credentials&scope=WEB
  ```

* 应用自身用户登录端点 *OAuth2 Client - password 模式* （即：己方或一方用户登录）

  > 🔔 系统初始化的用户账号及密码参考 `arch-user` 模块资源文件夹下面的 `data.sql`

  ```sh
  # 示例演示用户 wukong 使用 WEB 端登录获取访问、刷新令牌 
  POST http://localhost:9000/arch-iam/oauth2/token
  Content-Type: application/x-www-form-urlencoded
  Authorization: Basic YXJjaC13ZWI6c2VjcmV0d2Vi
  
  grant_type=password&scope=WEB&username=wukong&password=wukong
  ```

* 应用自身用户访问令牌刷新端点

  ```sh
  # 示例演示用户 wukong 使用 WEB 端刷新令牌 
  POST http://localhost:9000/arch-iam/oauth2/token
  Content-Type: application/x-www-form-urlencoded
  Authorization: Basic YXJjaC13ZWI6c2VjcmV0d2Vi
  
  grant_type=refresh_token&scope=WEB&refresh_token=kGrXegF9RW2zqwvMl_NvAc47YtIsVMy_eSV-P7MgmKPwPmS8Ov1mF0qLe7Z2L-FBmfMmGooQlkLHqdl0vn7QM_BRT88D5mL73W-7bEn6bByprP1uIyxS3gmo7sC2OJWk
  ```

* 登录用户访问受限资源测试端点

  ```sh
  # 示例演示用户 wukong 使用登录令牌认证方式访问 arch-app 下的受限资源 /ping
  GET http://localhost:9000/arch-app/ping
  Authorization: Bearer eyJraWQiOiI2ZTQxNTE4NS05YWU3LTRkZjgtYjU5MS0zZTU5NWZhYzgwNTIiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ3dWtvbmciLCJhdWQiOiJhcmNoLXdlYiIsIm5iZiI6MTcxODA5OTkzOCwic2NvcGUiOlsiV0VCIl0sInJvbGVzIjpbIlVTRVIiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwIiwiZXhwIjoxNzE4MTAwMjM4LCJpYXQiOjE3MTgwOTk5MzgsImp0aSI6ImQ5NGVkNzMwLTA2MjItNGM1OS05YzYyLTljMmJjMzlhNmNjZSJ9.SUrLC7Jy3azs6apyaZ3s6rZdQCX2WvZPtgPcEPTXpq2gBQYgXaj-fhn_iU59fvAuHWitfwTOl7dnlnTArSubAsXtDQjYrCLMViItXYbJFan683sZPkaxnUYVZlMNjQTcsvkH9YR13p2ZHf_YNN4dgnvS2Meup41L9uJLvfcfMAuRanZFzsoCUlGSkeGJyaHME5VeaVt-U8fDLsv9xAnWwDoXN4wCYf5CEBPm8zw5QPcc0Wg4CM7o8RaxdFFXuXjC7O8XgXMm48zj3j2GzVnrf6rZrl_zXri7aFm99RS_-FZcoIrS2NbCH27QUKtgwANV-mmeTwG04eDhcOS1mhHGew
  
  ```

* API 文档地址：http://localhost:9000/api-docs

  ![](https://raw.githubusercontent.com/ReionChan/PhotoRepo/master/arch/api-docs.png)

## 使用技术栈

* 服务注册与发现
  * Kubernetes CoreDNS
* 负载均衡
  * Kubernetes Service
* 服务容错
  * Istio DestinationRule
* 服务网关
  * Kubernetes Gateway API impl with Istio
* RPC
  * Open Feign with OkHttp Client
* 认证授权
  * Istio Security API
  * Spring Security OAuth2 仅提供 JWKS、己方登录支持
* 可观测性
  * Micrometer （统一埋点 API）
  * OpenTelemetry Java Agent （统一采集方式）
  * OpenTelemetry Collector （统一 OTLP 协议收集，隔离不同监控提供商）
    * 指标数据观测，包括不限于：Prometheus、Grafana、
    * 追踪数据观测，包括不限于：Jaeger、Zipkin、Tempo
    * 日志数据观测，包括不限于：ELK、Loki

## 编程式 → 云原生

|          | 基于 Spring Cloud 编程式                                     | 基于 Istio + Kubernetes 基础设施     | 进展 |
| -------- | ------------------------------------------------------------ | ------------------------------------ | ---- |
| 弹性伸缩 | ——                                                           | Autoscaling                          | ✅    |
| 服务发现 | Spring Cloud Alibaba Nacos / Netflix Eureka                  | Kubernetes CoreDNS                   | ✅    |
| 配置中心 | Spring Cloud Config Alibaba Nacos / Azure App Configuratioin | Kubernetes ConfigMap / Secret        | ✅    |
| 服务网关 | Spring Cloud Gateway                                         | Kubernetes Gateway API impl by Istio | ✅    |
| 负载均衡 | Spring Cloud Loadbalancer                                    | Kubernetes Service                   | ✅    |
| 服务安全 | Spring Security OAuth2                                       | Istio Security API                   | ✅    |
| 监控追踪 | Micrometer Tracing                                           | Istio Envoy with OpenTelemetry       | 🔜    |
| 熔断降级 | Spring Cloud Circuit Breaker with Resilience4J / Spring Retry | Istio DestinationRule                | ✅    |

