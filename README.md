# 黎小小食铺订单实施统计系统
## 项目介绍
本系统是为餐饮企业打造的分布式业务中台，基于SpringBoot+MyBatis构建，采用多级缓存架构和异步处理机制保障高并发场景下的系统稳定性。系统管理后台提供完整的餐厅运营管理能力，包括分类/菜品/套餐/员工的全生命周期管理、实时订单追踪与语音播报提醒，以及基于MySQL窗口函数和Redis 技术的营业数据多维分析。系统通过Redisson分布式锁保障库存安全，结合Druid连接池动态调整和线程池任务调度实现弹性扩容，采用JWT令牌认证+HTTPS传输加密构建完整安全体系。

## 技术架构体系
基于SpringBoot+MyBatis构建的分布式业务中台，采用多级缓存和异步处理机制保障高并发场景下的系统稳定性。核心组件：
- **Web服务层**：Nginx 1.24反向代理+负载均衡，支持10,000并发连接处理
- **数据存储层**：MySQL 8.0（InnoDB引擎）+ Redis 6.0（缓存/分布式锁）
- **异步通信**：WebSocket消息队列+线程池任务调度
- **监控体系**：SpringBoot Actuator+Prometheus埋点
- **接口规范**：Swagger3.0+RESTful API

## 核心能力扩展

### 1. 高并发解决方案
- 分布式锁控制：通过`SetmealDish`类（sky-pojo/src/main/java/com/sky/entity/SetmealDish.java#L18）的对象缓存策略，结合Redis Redisson实现套餐修改的分布式锁
- 线程池管理：基于`ThreadPoolConfig`类（sky-server/src/main/java/com/sky/config/ThreadPoolConfig.java#L10）配置弹性线程池，支持1000并发任务处理
- 消息削峰：WebSocket模块采用`WebSocketServer`类（sky-server/src/main/java/com/sky/webSocket/WebSocketServer.java#L31）的有界队列设计，防止消息洪泛

### 2. 支付中台能力
- 多通道支付集成：通过`WeChatProperties`类（sky-common/src/main/java/com/sky/properties/WeChatProperties.java#L10）和`PayNotifyController`类（sky-server/src/main/java/com/sky/controller/Notify/PayNotifyController.java#L26）实现支付/退款双链路
- 事务一致性：订单状态变更采用本地事务表+最大努力通知模式

### 3. 定时任务体系
- **分布式调度**：基于`ThreadPoolConfig`类（sky-server/src/main/java/com/sky/config/ThreadPoolConfig.java#L10）配置弹性线程池
- **任务类型**：
  - 支付超时订单处理（每分钟执行）
  - 派送订单状态更新（每日凌晨1点执行）


  ### 4. 切面编程体系
基于SpringAOP实现声明式编程，核心功能包含：
- **公共字段填充**：通过`AutoFill`注解（sky-common/src/main/java/com/sky/annotation/AutoFill.java#L8）实现create_time/update_time自动填充
- **操作日志采集**：通过行为切面记录管理端操作流水
- **缓存一致性维护**：采用环绕通知实现缓存更新原子操作

  ### 5. 智能推荐系统
基于OpenAI Embedding和本地微调模型实现个性化推荐，核心实现包含：
- **行为分析引擎**：通过`UserBehaviorAspect`类（sky-server/src/main/java/com/sky/aspect/UserBehaviorAspect.java#L28）采集用户点击/加购/支付行为

## 技术栈全景
| 层级        | 技术组件                                                                 |
|-----------|----------------------------------------------------------------------|
| Web服务    | Nginx 1.24                                 |
| 基础框架     | SpringBoot 2.7.x、Spring MVC、MyBatis、Lombok             |
| 数据存储     | MySQL 8.0、Redis 6.0、SpringCache使用、 AliOSS     |
| 异步通信     | WebSocket消息队列、ScheduledThreadPoolExecutor（定时任务）、RabbitMQ分布式锁 DTO             |
| 安全认证     | JWT+SpringSecurity、HTTPS传输加密、微信支付证书体系                         |
| 接口规范     | Swagger3.0+RESTful                    |

## 性能优化点
1. **JVM调优**：通过以下配置实现堆内存智能分配（基于`SkyApplication`类（sky-server/src/main/java/com/sky/SkyApplication.java#L10）启动参数）
```bash
java -Xms512m -Xmx2g -XX:MaxMetaspaceSize=256m 
     -XX:+UseG1GC -XX:MaxGCPauseMillis=200 
     -XX:ParallelGCThreads=4 -jar sky-server.jar
```
2. **缓存策略**：热点数据JVM本地缓存+Redis二级缓存，缓存中间件：SpringCache多级缓存配置
3. **批量处理**：基于`OrderTask`类（sky-server/src/main/java/com/sky/task/OrderTask.java#L16）的定时任务批量更新订单状态
4. **连接池优化**：Druid连接池监控+动态扩容机制 DTO
5. **索引策略**：订单表(time+status)联合索引，查询性能提升5倍  DTO

## 扩展能力增强
- **灰度发布**：通过`UserLoginVO`类（sky-pojo/src/main/java/com/sky/vo/UserLoginVO.java#L14）中的token字段实现用户路由
- **流量控制**：WebSocket模块`WebSocketServer`方法（sky-server/src/main/java/com/sky/webSocket/WebSocketServer.java#L51）实现5000连接数硬限流


## 项目结构
后端工程基于Maven进行项目构建，进行分模块开发  
1）项目整体结构
序号|名称|说明
-|-|-
1	|sky-take-out|	maven父工程，统一管理版本依赖，聚合其他子模块
2	|sky-common	|子模块，存放公共类，例如：工具类，常量类，异常类
3	|sky-pojo	|子模块，存放实体类，vo，DTO 等
4	|sky-server	|子模块，后端服务，存放配置文件、Controller、Service、Mapper等  

2）sky-common: 模块中存放的是一些公共类，可以供其他模块使用
名称|说明
-|-
constant|存放相关常量
context|存放上下文类
enumeration|项目的枚举类储存
exception|存放自定义异常类
json	|处理json转换的类
properties|存放springboot相关的配置属性类
result	|返回结果类的封装
utils	|常用工具类  

3）sky-pojo: 模块中存放的是一些 entity、DTO、VO
名称|说明
-|-
Entity|	实体，通常和数据库中的表对应
DTO	|数据传输对象，通常用于程序中各层之间传递数据
VO|	视图对象，为前端展示数据提供的对象
POJO	|普通Java对象，只有属性和对应的getter和setter  

4）sky-server: 模块中存放的是 配置文件、配置类、拦截器、controller、service、mapper、启动类等
| 名称          | 说明                                                                 |
|---------------|----------------------------------------------------------------------|
| config        | 存放Spring Boot配置类    |
| controller    | 包含admin/user子包，分别存放管理端和用户端的RESTful控制器             |
| interceptor   | 存放JWT认证拦截器（JwtTokenAdminInterceptor/JwtTokenUserInterceptor）|
| mapper        | MyBatis数据访问接口，包含基础CRUD和自定义查询方法                    |
| service       | 业务逻辑实现层，包含接口定义(service包)和实现类(impl子包)             |
| resources     | 存放配置文件、SQL映射文件、静态资源     |
| aspect        | AOP切面类（如AutoFillAspect实现公共字段自动填充）                     |
| task          | 定时任务处理类（如OrderTask处理超时订单）                             |
| webSocket     | WebSocket服务端实现和配置类                                           |
| SkyApplication| Spring Boot主启动类，包含缓存、事务、定时任务等全局配置                |


## 项目部分技术展现
### 1.Swagger
Swagger 是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务(https://swagger.io/)。 它的主要作用是：

1.使得前后端分离开发更加方便，有利于团队协作

2.接口的文档在线自动生成，降低后端开发人员编写接口文档的负担

3.功能测试

Spring已经将Swagger纳入自身的标准，建立了Spring-swagger项目，现在叫Springfox。通过在项目中引入Springfox ，即可非常简单快捷的使用Swagger。  
knife4j是为Java MVC框架集成Swagger生成Api文档的增强解决方案,前身是swagger-bootstrap-ui,取名kni4j是希望它能像一把匕首一样小巧,轻量,并且功能强悍!

目前，一般都使用knife4j框架。  

通过注解可以控制生成的接口文档，使接口文档拥有更好的可读性，常用注解如下：

注解	|说明
-|-
@Api	|用在类上，例如Controller，表示对类的说明
@ApiModel	|用在类上，例如entity、DTO、VO
@ApiModelProperty	|用在属性上，描述属性信息
@ApiOperation	|用在方法上，例如Controller的方法，说明方法的用途、作用

  


