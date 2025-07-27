import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

// API响应类型定义
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
}

// 存储请求类型
export interface StorageRequest {
  key: string;
  value: any;
  ttl?: number;
}

// 存储响应类型
export interface StorageResponse {
  key: string;
  value: any;
  ttl: number;
  timestamp: number;
}

// 集群节点信息
export interface ClusterNode {
  id: string;
  host: string;
  port: number;
  status: string;
  role: string;
  lastHeartbeat: number;
}

// 缓存统计信息
export interface CacheStats {
  hitCount: number;
  missCount: number;
  hitRate: number;
  keyCount: number;
  timestamp: number;
}

// 系统监控指标
export interface SystemMetrics {
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
  networkIO: {
    bytesIn: number;
    bytesOut: number;
  };
  timestamp: number;
}

class ApiService {
  private api: AxiosInstance;
  private token: string | null = null;

  constructor() {
    this.api = axios.create({
      baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api/v1',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // 请求拦截器 - 添加认证token
    this.api.interceptors.request.use(
      (config) => {
        if (this.token) {
          config.headers.Authorization = `Bearer ${this.token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // 响应拦截器 - 处理错误
    this.api.interceptors.response.use(
      (response) => {
        return response;
      },
      (error) => {
        if (error.response?.status === 401) {
          // Token过期，清除本地token
          this.token = null;
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );

    // 从localStorage恢复token
    const savedToken = localStorage.getItem('token');
    if (savedToken) {
      this.token = savedToken;
    }
  }

  // 设置认证token
  setToken(token: string) {
    this.token = token;
    localStorage.setItem('token', token);
  }

  // 清除认证token
  clearToken() {
    this.token = null;
    localStorage.removeItem('token');
  }

  // 用户认证
  async login(username: string, password: string): Promise<ApiResponse<{ token: string; user: any }>> {
    const response = await this.api.post('/auth/login', { username, password });
    return response.data;
  }

  async register(username: string, password: string, email: string): Promise<ApiResponse<any>> {
    const response = await this.api.post('/auth/register', { username, password, email });
    return response.data;
  }

  async logout(): Promise<ApiResponse<any>> {
    const response = await this.api.post('/auth/logout');
    this.clearToken();
    return response.data;
  }

  // 存储操作
  async storeData(request: StorageRequest): Promise<ApiResponse<StorageResponse>> {
    const response = await this.api.post('/storage', request);
    return response.data;
  }

  async getData(key: string): Promise<ApiResponse<StorageResponse>> {
    const response = await this.api.get(`/storage/${key}`);
    return response.data;
  }

  async deleteData(key: string): Promise<ApiResponse<any>> {
    const response = await this.api.delete(`/storage/${key}`);
    return response.data;
  }

  async getAllKeys(): Promise<ApiResponse<string[]>> {
    const response = await this.api.get('/storage/keys');
    return response.data;
  }

  async batchStore(requests: StorageRequest[]): Promise<ApiResponse<StorageResponse[]>> {
    const response = await this.api.post('/storage/batch', requests);
    return response.data;
  }

  // 文件上传
  async uploadFile(file: File, key?: string): Promise<ApiResponse<StorageResponse>> {
    const formData = new FormData();
    formData.append('file', file);
    if (key) {
      formData.append('key', key);
    }

    const response = await this.api.post('/storage/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  async downloadFile(key: string): Promise<Blob> {
    const response = await this.api.get(`/storage/download/${key}`, {
      responseType: 'blob',
    });
    return response.data;
  }

  // 集群管理
  async getClusterStatus(): Promise<ApiResponse<{ nodes: ClusterNode[]; leader: string }>> {
    const response = await this.api.get('/cluster/status');
    return response.data;
  }

  async getClusterNodes(): Promise<ApiResponse<ClusterNode[]>> {
    const response = await this.api.get('/cluster/nodes');
    return response.data;
  }

  async addClusterNode(host: string, port: number): Promise<ApiResponse<ClusterNode>> {
    const response = await this.api.post('/cluster/nodes', { host, port });
    return response.data;
  }

  async removeClusterNode(nodeId: string): Promise<ApiResponse<any>> {
    const response = await this.api.delete(`/cluster/nodes/${nodeId}`);
    return response.data;
  }

  // 缓存监控
  async getCacheStats(): Promise<ApiResponse<CacheStats>> {
    const response = await this.api.get('/cache/monitoring/stats');
    return response.data;
  }

  async getCachePerformance(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/cache/monitoring/performance');
    return response.data;
  }

  async getCacheHealth(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/cache/monitoring/health');
    return response.data;
  }

  async getCacheHotData(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/cache/monitoring/hot-data');
    return response.data;
  }

  // 系统监控
  async getSystemMetrics(): Promise<ApiResponse<SystemMetrics>> {
    const response = await this.api.get('/monitoring/metrics');
    return response.data;
  }

  async getSystemHealth(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/monitoring/health');
    return response.data;
  }

  async getSystemInfo(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/monitoring/info');
    return response.data;
  }

  // 搜索功能
  async searchKeys(query: string, limit?: number): Promise<ApiResponse<string[]>> {
    const params: any = { query };
    if (limit) {
      params.limit = limit;
    }
    const response = await this.api.get('/storage/search', { params });
    return response.data;
  }

  // 分片和副本管理
  async getShardingStats(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/sharding/cluster/stats');
    return response.data;
  }

  async getReplicationStats(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/sharding/replication/stats');
    return response.data;
  }

  // 通用请求方法
  async request<T>(config: AxiosRequestConfig): Promise<ApiResponse<T>> {
    const response = await this.api.request(config);
    return response.data;
  }

  // AI服务相关方法
  async processAIQuery(query: string): Promise<ApiResponse<string>> {
    const response = await this.api.post('/ai/query', { query });
    return response.data;
  }

  async intelligentQA(question: string, context?: any): Promise<ApiResponse<any>> {
    const response = await this.api.post('/ai/qa', { question, context });
    return response.data;
  }

  async documentBasedQA(question: string, documentIds: string[]): Promise<ApiResponse<string>> {
    const response = await this.api.post('/ai/qa/documents', { question, documentIds });
    return response.data;
  }

  async semanticSearch(query: string, filters?: any): Promise<ApiResponse<any[]>> {
    const response = await this.api.post('/ai/search', { query, filters });
    return response.data;
  }

  async generateIntelligentSummary(content: string, summaryType: string = 'standard'): Promise<ApiResponse<string>> {
    const response = await this.api.post('/ai/summary', { content, summaryType });
    return response.data;
  }

  async autoClassifyAndTag(content: string): Promise<ApiResponse<any>> {
    const response = await this.api.post('/ai/classify', { content });
    return response.data;
  }

  async analyzeRelevance(content1: string, content2: string): Promise<ApiResponse<any>> {
    const response = await this.api.post('/ai/relevance', { content1, content2 });
    return response.data;
  }

  // 向量数据库相关方法
  async storeDocumentVector(documentId: string, content: string, metadata?: any): Promise<ApiResponse<string>> {
    const response = await this.api.post('/ai/vectors/store', { documentId, content, metadata });
    return response.data;
  }

  async searchSimilarDocuments(query: string, topK: number = 5): Promise<ApiResponse<any[]>> {
    const response = await this.api.post('/ai/vectors/search', { query, topK });
    return response.data;
  }

  async batchStoreDocuments(documents: any[]): Promise<ApiResponse<number>> {
    const response = await this.api.post('/ai/vectors/batch', { documents });
    return response.data;
  }

  async getVectorStats(): Promise<ApiResponse<any>> {
    const response = await this.api.get('/ai/vectors/stats');
    return response.data;
  }

  // AI服务系统分析
  async getSystemAnalysis(): Promise<ApiResponse<string>> {
    const response = await this.api.get('/ai/analyze/system');
    return response.data;
  }

  async getDataSummary(dataKey: string): Promise<ApiResponse<string>> {
    const response = await this.api.get(`/ai/summary/${dataKey}`);
    return response.data;
  }

  async getOptimizationSuggestions(): Promise<ApiResponse<string>> {
    const response = await this.api.get('/ai/suggestions');
    return response.data;
  }

  async checkAIHealth(): Promise<ApiResponse<boolean>> {
    const response = await this.api.get('/ai/health');
    return response.data;
  }
}

// 导出单例实例
export const apiService = new ApiService();
export default apiService; 