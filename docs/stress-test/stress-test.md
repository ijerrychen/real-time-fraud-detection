# 压力测试
## 简介
这是一个使用开源的Locust做的压力测试。 Locust是一个开源的性能测试工具，主要用于模拟用户行为来测试web站点或其他系统的负载能力‌。它允许用户使用Python编写测试脚本，定义用户的行为，并通过一个基于Web的用户界面实时监控测试结果‌
## 测试报告
[stress-test-report](https://ijerrychen.github.io/real-time-fraud-detection/stress-test-report.html) (链接打开慢，请耐心等待)
```
1. **用户模拟**
  - 持续5-10分钟压力测试
  - 每秒新增100用户
  - 峰值1000并发用户
  - 每个用户包含思考时间：0.1-0.5秒
  - 平均请求频率 ≈ 1÷(0.3) = 3.3 请求/秒/用户
  - 理论最大 QPS : 1000用户 × 3.3请求/秒 ≈ 3,300 QPS
  - 实际 QPS
   - 取决于服务端响应能力
   - 典型范围：1,000-3,000 QPS

2. **请求特征**
  - JSON格式请求体
  - 混合3种业务场景
    - 正常交易 - 权重 10 (77%) - 500随机金额
    - 大额交易 - 权重 2 (15%)  - 10000高金额
    - 问题交易 - 权重 1 (8%) - 空字段/负金额/无效时间
  - 基于实际业务的比例分配

3. **输出报告** (详见根目录下 stress-test-report.html)
  - HTML格式结果报告 
  - 包含：
    - 总请求数
    - 成功率/失败率
        | Type       | Name           | # Requests | # Fails | Average (ms) | Min (ms) | Max (ms) | Average size (bytes) | RPS     | Failures/s |
        |------------|----------------|-----------:|--------:|-------------:|---------:|---------:|---------------------:|--------:|-----------:|
        | POST       | `/transactions`| 310,622    | 45      | 520.59       | 2        | 3,227    | 120.99              | 1029.48 | 0.15       |
        |            |  Aggregated    | 310,622    | 45      | 520.59       | 2        | 3,227    | 120.99              | 1029.48 | 0.15       |

    - 响应时间分布
        | Method     | Name          | 50%ile (ms) | 60%ile (ms) | 70%ile (ms) | 80%ile (ms) | 90%ile (ms) | 95%ile (ms) | 99%ile (ms) | 100%ile (ms) |
        |------------|---------------|------------:|------------:|------------:|------------:|------------:|------------:|------------:|-------------:|
        | POST       | `/transactions` | 500         | 540         | 570         | 610         | 660         | 740         | 1100        | 3200         |
        |            |  Aggregate      | 500         | 540         | 570         | 610         | 660         | 740         | 1100        | 3200         |    

    - RPS时间曲线
      详见压测报告

```
## HPA验证
【符合预期】 由2个副本扩容至8个副本，符合HPA配置的初始2个，最大8个
### 集群状态
```
(locust-env) [ec2-user@ip-172-31-27-202 ~]$ kubectl get pods         
NAME                                         READY   STATUS    RESTARTS   AGE
fraud-detection-deployment-6c4b96475-4dj8c   1/1     Running   0          5m28s
fraud-detection-deployment-6c4b96475-5sbbg   1/1     Running   0          6m28s
fraud-detection-deployment-6c4b96475-bts9q   1/1     Running   0          6m28s
fraud-detection-deployment-6c4b96475-d2vtl   1/1     Running   0          21m
fraud-detection-deployment-6c4b96475-f86dz   1/1     Running   0          46h
fraud-detection-deployment-6c4b96475-gh9b9   1/1     Running   0          5m13s
fraud-detection-deployment-6c4b96475-ls2zc   1/1     Running   0          5m28s
fraud-detection-deployment-6c4b96475-px2s6   1/1     Running   0          6m28
```
### 查看HPA状态
```
- HPA状态
     (locust-env) [ec2-user@ip-172-31-27-202 ~]$ # 查看HPA状态
     kubectl describe hpa fraud-detection-hpa
     Name:                                                  fraud-detection-hpa
     Namespace:                                             default
     Labels:                                                <none>
     Annotations:                                           <none>
     CreationTimestamp:                                     Sun, 01 Jun 2025 04:13:32 +0000
     Reference:                                             Deployment/fraud-detection-deployment
     Metrics:                                               ( current / target )
     resource cpu on pods  (as a percentage of request):  0% (2m) / 75%
     Min replicas:                                          2
     Max replicas:                                          8
     Behavior:
     Scale Up:
     Stabilization Window: 0 seconds
     Select Policy: Max
     Policies:
     - Type: Pods     Value: 4    Period: 15 seconds
     - Type: Percent  Value: 100  Period: 15 seconds
       Scale Down:
       Stabilization Window: 300 seconds
       Select Policy: Max
       Policies:
     - Type: Percent  Value: 20  Period: 60 seconds
       Deployment pods:       7 current / 6 desired
       Conditions:
       Type            Status  Reason              Message
  ----            ------  ------              -------
AbleToScale     True    SucceededRescale    the HPA controller was able to update the target scale to 6
ScalingActive   True    ValidMetricFound    the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
ScalingLimited  False   DesiredWithinRange  the desired count is within the acceptable range
Events:
Type    Reason             Age                 From                       Message
  ----    ------             ----                ----                       -------
Normal  SuccessfulRescale  31m (x2 over 39m)   horizontal-pod-autoscaler  New size: 3; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  30m (x2 over 10h)   horizontal-pod-autoscaler  New size: 4; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  25m (x2 over 10h)   horizontal-pod-autoscaler  New size: 3; reason: All metrics below target
Normal  SuccessfulRescale  24m (x3 over 10h)   horizontal-pod-autoscaler  New size: 2; reason: All metrics below target
Normal  SuccessfulRescale  8m18s               horizontal-pod-autoscaler  New size: 5; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  7m17s               horizontal-pod-autoscaler  New size: 7; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  7m2s (x2 over 10h)  horizontal-pod-autoscaler  New size: 8; reason: cpu resource utilization (percentage of request) above target
Normal  SuccessfulRescale  47s (x2 over 10h)   horizontal-pod-autoscaler  New size: 7; reason: All metrics below target
Normal  SuccessfulRescale  2s (x2 over 10h)    horizontal-pod-autoscaler  New size: 6; reason: All metrics below target
```

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
[stress-test-report](https://ijerrychen.github.io/real-time-fraud-detection/stress-test-report.html) (链接打开慢，请耐心等待)
亦可下载根目录下[stress-test-report.html](../../stress-test-report.html)，在本地浏览器中打开report。