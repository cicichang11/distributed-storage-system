
本目录包含了 Raft Storage 项目的多种 Kubernetes 部署策略配置和自动化脚本。

## 📁 目录结构

```
deployment-strategies/
├── README.md                    # 本文档
├── rolling-update.yaml          # 滚动更新配置
├── blue-green.yaml             # 蓝绿部署配置
├── canary.yaml                 # 金丝雀发布配置
├── deploy-rolling-update.sh    # 滚动更新脚本
├── deploy-blue-green.sh        # 蓝绿部署脚本
└── deploy-canary.sh            # 金丝雀部署脚本
```

## 🚀 部署策略概述

### 1. 滚动更新 (Rolling Update)

**特点：**
- 逐步替换旧版本Pod，确保服务不中断
- 最小化停机时间
- 资源消耗相对较低
- 适合大多数生产环境

**使用场景：**
- 日常版本更新
- 小版本补丁发布
- 向后兼容的更新

### 2. 蓝绿部署 (Blue-Green)

**特点：**
- 同时运行两个完全相同的生产环境
- 瞬间切换，零停机时间
- 快速回滚能力
- 资源消耗较高（需要双倍资源）

**使用场景：**
- 重大版本发布
- 关键业务系统更新
- 需要快速回滚的场景

### 3. 金丝雀发布 (Canary)

**特点：**
- 渐进式发布，控制风险
- 可配置流量分配比例
- 实时监控和自动回滚
- 支持A/B测试

**使用场景：**
- 新功能验证
- 性能影响评估
- 风险控制要求高的更新

## 🛠️ 快速开始

### 滚动更新部署

```bash
# 基本用法
./deploy-rolling-update.sh

# 指定版本
./deploy-rolling-update.sh v1.10.0
```

### 蓝绿部署

```bash
# 基本用法
./deploy-blue-green.sh

# 指定版本和环境
./deploy-blue-green.sh v1.10.0 raft-api blue green
```

### 金丝雀发布

```bash
# 基本用法（10%流量）
./deploy-canary.sh

# 指定流量比例
./deploy-canary.sh v1.10.0 raft-api 20
```

## 📊 监控和验证

### 健康检查

```bash
# 检查Pod状态
kubectl get pods -n raft-storage -o wide

# 健康检查API
kubectl port-forward service/raft-api-service -n raft-storage 8080:80
curl http://localhost:8080/api/v1/monitoring/health
```

### 日志监控

```bash
# 实时查看日志
kubectl logs -f deployment/raft-api-deployment -n raft-storage

# 查看事件
kubectl get events -n raft-storage --sort-by='.lastTimestamp'
```

## 🔄 回滚操作

### 滚动更新回滚

```bash
# 回滚到上一个版本
kubectl rollout undo deployment/raft-api-deployment -n raft-storage
```

### 蓝绿部署回滚

```bash
# 快速回滚（切换服务指向）
kubectl patch service raft-api-service -n raft-storage \
  -p '{"spec":{"selector":{"environment":"blue"}}}'
```

### 金丝雀回滚

```bash
# 停止金丝雀流量
kubectl scale deployment raft-api-canary -n raft-storage --replicas=0
```

## ⚠️ 注意事项

### 安全考虑

1. **镜像安全**：确保使用经过安全扫描的镜像
2. **权限控制**：使用最小权限原则配置ServiceAccount
3. **网络策略**：配置NetworkPolicy限制Pod间通信
4. **密钥管理**：使用Kubernetes Secrets管理敏感信息

### 资源管理

1. **资源限制**：为所有容器设置合适的资源请求和限制
2. **存储管理**：确保PVC有足够空间且配置了备份
3. **节点选择**：使用节点亲和性确保Pod分布
4. **容忍度配置**：处理节点故障和维护场景

## 🚨 故障排查

### 常见问题

**1. 部署失败**
```bash
# 检查Pod状态和事件
kubectl describe pod <pod-name> -n raft-storage

# 查看容器日志
kubectl logs <pod-name> -n raft-storage -c api
```

**2. 健康检查失败**
```bash
# 检查探针配置
kubectl get deployment raft-api-deployment -n raft-storage -o yaml | grep -A 10 probe

# 手动测试健康检查
kubectl exec -it <pod-name> -n raft-storage -- curl localhost:8080/api/v1/monitoring/health
```

## 📚 最佳实践

1. **渐进式发布**：从开发环境→测试环境→预生产环境→生产环境
2. **自动化测试**：集成单元测试、集成测试、端到端测试
3. **监控驱动**：基于监控数据决定是否继续发布
4. **文档记录**：详细记录每次发布的变更和结果
5. **团队协作**：建立清晰的发布流程和责任分工 
# Kubernetes 部署策略
 