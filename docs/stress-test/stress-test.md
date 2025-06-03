# 压力测试
## 简介
这是一个使用开源的Locust做的压力测试。 Locust是一个开源的性能测试工具，主要用于模拟用户行为来测试web站点或其他系统的负载能力‌。它允许用户使用Python编写测试脚本，定义用户的行为，并通过一个基于Web的用户界面实时监控测试结果‌
## 测试报告
[report.html](report.html)
## 测试详细过程
### 测试脚本
- [locustfile.py](locustfile.py)
### 执行环境
- EC2一台或数台
- Python3环境
- locust
### 执行步骤
- 1.更新系统并安装必要工具
```
sudo yum update -y
sudo yum install -y python3 python3-pip jq awscli
```

- 2.安装Locust和Boto3
```
sudo pip3 install locust boto3
```

- 3.验证安装
```
python3 --version
locust --version
aws --version
```

- 4.设置测试环境变量
```
# 获取ELB端点
ELB_ENDPOINT=$(kubectl get svc fraud-detection-service -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
echo "服务端点: http://$ELB_ENDPOINT/transactions"

# 获取SQS队列URL (如果适用)
QUEUE_NAME="fraud-detection-queue"
QUEUE_URL=$(aws sqs get-queue-url --queue-name $QUEUE_NAME --query 'QueueUrl' --output text)
```

- 5.创建Locust测试脚本
[locustfile.py](locustfile.py)

- 6.创建监控脚本
[monitor.sh](monitor.sh)
- 7.执行压力测试
  - 在第一个终端中启动监控:
```
./monitor.sh
```
  - 在第二个终端中启动Locust测试:
```
# 安装报告工具
pip install locust-plugins

# 运行测试并生成HTML报告
locust -f locustfile.py --headless -u 1000 -r 100 -t 10m \
--html report.html \
-H http://$ELB_ENDPOINT
```
- 8.查看HTML报告
下载report.html，在本地浏览器中打开report.html。