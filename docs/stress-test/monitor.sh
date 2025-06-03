#!/bin/bash
# 集群监控脚本 (在压力测试期间运行)

watch -n 5 "
echo '======= Pod状态 =======';
kubectl get pods -o wide;
echo '\n======= HPA状态 =======';
kubectl get hpa;
echo '\n======= 节点资源使用 =======';
kubectl top nodes;
echo '\n======= Pod资源使用 =======';
kubectl top pods;
echo '\n======= SQS队列状态 =======';
aws sqs get-queue-attributes \
  --queue-url $QUEUE_URL \
  --attribute-names ApproximateNumberOfMessages ApproximateNumberOfMessagesNotVisible \
  --query 'Attributes' --output table;
echo '\n======= ELB指标 =======';
ELB_NAME=$(aws elbv2 describe-load-balancers --query "LoadBalancers[?contains(DNSName, '$ELB_ENDPOINT')].LoadBalancerArn" --output text);
aws cloudwatch get-metric-statistics \
  --namespace AWS/ApplicationELB \
  --metric-name RequestCount \
  --dimensions Name=LoadBalancer,Value=$ELB_NAME \
  --start-time $(date -u -v-5M +%Y-%m-%dT%H:%M:%SZ) \
  --end-time $(date -u +%Y-%m-%dT%H:%M:%SZ) \
  --period 60 \
  --statistics Sum \
  --query 'Datapoints[0].Sum' --output text | xargs -0 printf '最近5分钟请求数: %.0f\n';
"