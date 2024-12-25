# 苍穹外卖
## 项目介绍

本项目（苍穹外卖）是专门为餐饮企业（餐厅、饭店）定制的一款软件产品，包括 系统管理后台 和 小程序端应用 两部分。其中系统管理后台主要提供给餐饮企业内部员工使用，可以对餐厅的分类、菜品、套餐、订单、员工等进行管理维护，对餐厅的各类数据进行统计，同时也可进行来单语音播报功能。小程序端主要提供给消费者使用，可以在线浏览菜品、添加购物车、下单、支付、催单等。

## 1.开发环境搭建

### 1.1 项目结构
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
名称|说明
-|-
config|存放配置类
controller|存放controller类
interceptor	|存放拦截器类
mapper	|存放mapper接口
service	|存放service类
SkyApplication|启动类


### 1.2前后端联调
后端的初始工程中已经实现了登录功能，直接进行前后端联调测试即可



### 1.3导入接口文档


### 1.4 Swagger
