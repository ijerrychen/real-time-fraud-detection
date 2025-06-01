# 一个基于简单规则和黑名单的金融交易欺诈检测服务

## 概要描述
当发起一笔金融交易时，实时检测交易的金额是否大于某个阈值或处在黑名单中，当命中规则时，发送短信、邮件等多种方式通知出来。

## 快速入门
本工程是一个基于AWS Cloud原生服务构建的应用，部署在AWS EKS上，依赖AWS SQS服务。因此，在运行前需要先准备AWS Cloud资源，包AWS EC2，AWS EKS集群，AWS SQS，并创建相应的IMA角色、权限、策略，以及EKS ServiceAccount。正确配置各种资源的权限是服务正常运行的前提。
### 1.基础环境
- 该工程基于SpringBoot3.x开发
- Java版本 JDK 21
- Maven版本 maven 3.5+
- AWS SDK for JAVA 2.x
- Spring Cloud for AWS 3.x
#### 先查看工程中的pom文件，确认以上核心组件，避免版本不兼容的问题
### 2.如何运行
#### 2.1 本地工程运行
- 首先将/src/main/resources/secrets.properties.example 改为secrets.properties，并将更换成你的aws身份凭据。在AwsConfig配置类中会构建aws凭据链。正确凭据链，是SQS正常连接和读写的关键。
- 进入到项目根目录，执行 mvn clean package，从target目录下获取jar包
![img.png](https://media.githubusercontent.com/media/ijerrychen/lfs/refs/heads/master/rtf/images/jar.png)
- jar包在任意目录下，执行java -jar *.jar即可运行
#### 2.2 AWS EKS集群运行
- 创建EKS集群，创建ServiceAccount，IAM Role，策略，权限等，绑定Account与IAM Role，在配置正确的情况下服务运行在pod中，可以以绑定的IAM身份访问SQS。
- 创建和选择VPC子网，其中public子网配置IGW网关，private子网需要配置NAT网关，这样pod可以访问公网。按需要给EKS集群分配子网类型。子网分配需要在不同的可用区。
- 详细看根目录下的deployment.xml文件，镜像从docker bub拉取。

### 3.视频说明
[点击查看完整视频介绍](https://ijerrychen.github.io/real-time-fraud-detection/video-introduce.html)
[下载演示视频](https://work-video-2025.oss-cn-guangzhou.aliyuncs.com/real-time-fraud-detection/real-time-fraud-detection-app.mp4)
#### 演示说明：
- 项目需求简述
- 技术栈-开发环境-开发工具-库-运行环境说明
- 业务流程-系统架构简介
- 系统流程-功能演示
- 核心代码逻辑说明
### 4.配置说明
#### 4.1 欺诈规则
- 欺诈规则配置在src/main/resource目录下的fraud-rules.properties文件中，可以按需求和部署环境调整
```
# rules for detection
rule.thresholdAmount=10000
rule.suspiciousAccounts=pandas,monkey,tiger
```
- aws相关配置在src/main/resource下的application.properties和secrets.properties文件里，包括
```
# ---application.properties---
# AWS configuration
aws.region=[YOUR REGION]

# SQS queue name
aws.sqs.queueUrl.prefix=https://sqs.[YOUR REGION].amazonaws.com/[YOUR ACCESS KEY]/
app.sqs.queue-name=[YOUR QUEUE NAME]

# ---secrets.properties---
# 注意：在.gitignore文件中将secrets.properties加入，该文件仅用于本地测试使用，避免敏感信息泄露
aws.accessKeyId=REPLACE_WITH_YOUR_AWS_ACCESS_KEY
aws.secretKey=REPLACE_WITH_YOUR_AWS_SECRET_KEY
```

### Docker部署
docker run -d --name fraud-detection-app -p 8080:8080 --restart always chenjie1984/real-time-fraud-detection:latest --spring.profiles.active=dev
#### 注意：如果使用windows系统，请安装[docker desktop](https://docs.docker.com/desktop/setup/install/windows-install/)

### AWS EKS部署
- [部署清单][deployment.yaml](deployment.yaml)

### 项目详情
- [需求详情][requirements.md](docs/requirements.md)
- [设计详情][design.md](docs/design.md)

### 后续扩展
- 按需对接真实消息通知服务，并且对对不同的通知方式提供不同的实现类。
- 增加对欺诈规则的数据库和管理工作台配置管理
- 增加复杂规则的配置和检测逻辑
- 增加基于规则引擎的规则检测机制
- 支持更加完备的自动化流水线部署，包基础环境的部署和应用部署
- 增加完备的服务监控和运维通知截止
- 更加完备的测试方案，包括集成测试、端到端测试手段

