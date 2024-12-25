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
需要将接口文档导入到管理平台，为我们后面业务开发做好准备  
将资料中提供的项目接口导入YApi

### 1.4 Swagger
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

# 2.需求分析和设计
本项目约定：

管理端发出的请求，统一使用**/admin**作为前缀。
用户端发出的请求，统一使用**/user**作为前缀。  

## 2.1新增员工
新增员工，其实就是将我们新增页面录入的员工数据插入到employee表。

employee表结构：


| **字段名**  | **数据类型** | **说明**     | **备注**    |
| ----------- | ------------ | ------------ | ----------- |
| id          | bigint       | 主键         | 自增        |
| name        | varchar(32)  | 姓名         |             |
| username    | varchar(32)  | 用户名       | 唯一        |
| password    | varchar(64)  | 密码         |             |
| phone       | varchar(11)  | 手机号       |             |
| sex         | varchar(2)   | 性别         |             |
| id_number   | varchar(18)  | 身份证号     |             |
| status      | Int          | 账号状态     | 1正常 0锁定 |
| create_time | Datetime     | 创建时间     |             |
| update_time | datetime     | 最后修改时间 |             |
| create_user | bigint       | 创建人id     |             |
| update_user | bigint       | 最后修改人id |             |

其中，employee表中的status字段已经设置了默认值1，表示状态正常。




