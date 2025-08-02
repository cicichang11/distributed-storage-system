# 🚀 Distributed Storage System with Raft Consensus

A production-ready distributed storage system built with **Raft consensus algorithm**, featuring a modern React frontend, Spring Boot API, and comprehensive monitoring stack.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.0+-blue.svg)](https://reactjs.org/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-1.25+-326CE5.svg)](https://kubernetes.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │  Spring Boot API│    │  Raft Consensus │
│   (Port 3000)   │◄──►│   (Port 8080)   │◄──►│   (3+ Nodes)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Redis Cache   │    │  Object Storage │    │  Prometheus     │
│   (Port 6379)   │    │   (Local/Cloud) │    │   Monitoring    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## ✨ Features

### 🎯 Core Features
- **Distributed Key-Value Store** with Raft consensus
- **File Upload/Download** with metadata management
- **Real-time Monitoring** with Prometheus & Grafana
- **AI-Powered Features** (DeepSeek integration)
- **JWT Authentication** with role-based access
- **Rate Limiting** and circuit breakers
- **Auto-scaling** Kubernetes deployment

### 🚀 Advanced Capabilities
- **Smart Caching** with Redis Sentinel
- **Object Storage** with S3-compatible API
- **Intelligent Data Analysis** via AI
- **Fault Tolerance** with automatic failover
- **Load Balancing** and health checks
- **Comprehensive Logging** and tracing

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Backend API** | Spring Boot | 3.2.0 |
| **Frontend** | React + TypeScript | 18.0+ |
| **Consensus** | Raft Algorithm | Custom |
| **Cache** | Redis + Sentinel | 7.0+ |
| **Monitoring** | Prometheus + Grafana | Latest |
| **Container** | Docker | Latest |
| **Orchestration** | Kubernetes | 1.25+ |
| **AI Integration** | DeepSeek API | Latest |

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Kubernetes cluster (optional)

### 1. Clone the Repository
```bash
git clone https://github.com/cicichang11/distributed-storage-system
cd distributed-storage-system
```

### 2. Build Core Components
```bash
# Build Raft consensus core
mvn clean install -f distribute-java-core/pom.xml

# Build cluster management
mvn clean install -f distribute-java-cluster/pom.xml

# Build Spring Boot API
mvn clean install -f spring-boot-api/pom.xml
```

### 3. Start Services

#### Option A: Docker Compose (Recommended)
```bash
# Start all services
docker-compose up -d

# Check services
docker-compose ps
```

#### Option B: Manual Start
```bash
# Start Redis
docker run -d -p 6379:6379 redis:7-alpine

# Start Spring Boot API
mvn spring-boot:run -f spring-boot-api/pom.xml

# Start React Frontend
cd frontend && npm start
```

### 4. Access Services
- **Frontend Dashboard**: http://localhost:3000
- **API Documentation**: http://localhost:8080/api/v1/swagger-ui.html
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin/admin123)

## 📚 API Usage

### Basic Storage Operations
```bash
# Store data
curl -X POST http://localhost:8080/api/v1/storage/set \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"key": "user:123", "value": "John Doe"}'

# Retrieve data
curl http://localhost:8080/api/v1/storage/get/user:123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Upload file
curl -X POST http://localhost:8080/api/v1/storage/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@document.pdf" \
  -F "key=documents/report.pdf"
```

### Cluster Management
```bash
# Get cluster status
curl http://localhost:8080/api/v1/cluster/status \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get system metrics
curl http://localhost:8080/api/v1/monitoring/metrics \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🏭 Production Deployment

### Kubernetes Deployment
```bash
# Deploy to Kubernetes
kubectl apply -f k8s/namespaces/
kubectl apply -f k8s/deployments/
kubectl apply -f k8s/services/
kubectl apply -f k8s/ingress/

# Or use Helm
helm install raft-storage helm/raft-storage/
```

### Docker Deployment
```bash
# Build images
docker build -t distributed-storage-api ./spring-boot-api
docker build -t distributed-storage-frontend ./frontend

# Run with docker-compose
docker-compose -f docker-compose.prod.yml up -d
```

## 📊 Monitoring & Observability

### Metrics Dashboard
- **System Metrics**: CPU, Memory, Disk usage
- **Application Metrics**: Request rate, response time, error rate
- **Raft Metrics**: Leader election, log replication, consensus
- **Storage Metrics**: Read/write operations, cache hit rate

### Alerting
- **High CPU/Memory usage**
- **Raft cluster health issues**
- **API response time degradation**
- **Storage capacity warnings**

## 🔧 Configuration

### Environment Variables
```bash
# API Configuration
SPRING_PROFILES_ACTIVE=prod
RAFT_CLUSTER_NODES=node1:8051,node2:8052,node3:8053
JWT_SECRET=your-secret-key
REDIS_HOST=redis-sentinel
REDIS_PORT=6379

# AI Integration
DEEPSEEK_API_KEY=your-api-key
AI_ENABLED=true

# Monitoring
PROMETHEUS_ENABLED=true
GRAFANA_ENABLED=true
```

### Application Properties
```yaml
# application.yml
server:
  port: 8080

raft:
  cluster:
    nodes:
      - id: 1
        host: node1
        port: 8051
      - id: 2
        host: node2
        port: 8052
      - id: 3
        host: node3
        port: 8053

monitoring:
  prometheus:
    enabled: true
  grafana:
    enabled: true
```

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific module tests
mvn test -f distribute-java-core/pom.xml
mvn test -f spring-boot-api/pom.xml
```

### Integration Tests
```bash
# Test Raft consensus
cd distribute-java-cluster/env/example1
./bin/run_server.sh &
./bin/run_client.sh

# Test API endpoints
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### Load Testing
```bash
# Run performance tests
node scripts/performance-test.js

# Or use Apache Bench
ab -n 1000 -c 10 http://localhost:8080/api/v1/storage/get/test-key
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and add tests
4. Commit your changes: `git commit -m 'Add amazing feature'`
5. Push to the branch: `git push origin feature/amazing-feature`
6. Open a Pull Request

## 🙏 Acknowledgments

- **Raft Consensus Algorithm** - Original paper by Diego Ongaro and John Ousterhout
- **Spring Boot** - For the excellent framework
- **React** - For the powerful frontend library
- **Kubernetes** - For container orchestration
- **Prometheus & Grafana** - For monitoring and observability


⭐ **Star this repository if you find it useful!**

🔗 **Connect with me**: [LinkedIn](https://www.linkedin.com/in/cici-chang/)
