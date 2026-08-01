// API Utility Functions

class APIClient {
    constructor(baseUrl = 'http://localhost') {
        this.baseUrl = baseUrl;
        this.timeout = 30000; // 30 seconds timeout
    }

    setBaseUrl(url) {
        this.baseUrl = url;
    }

    getBaseUrl() {
        return this.baseUrl;
    }

    async getStudents() {
        return this.fetchWithTimeout('/api/students');
    }

    async getStudentById(id) {
        return this.fetchWithTimeout(`/api/students/${id}`);
    }

    async getSubjects() {
        return this.fetchWithTimeout('/api/subjects');
    }

    async getSubjectById(id) {
        return this.fetchWithTimeout(`/api/subjects/${id}`);
    }

    async getResults() {
        return this.fetchWithTimeout('/api/results');
    }

    async getResultById(id) {
        return this.fetchWithTimeout(`/api/results/${id}`);
    }

    async checkHealth(port) {
        try {
            const startTime = performance.now();
            const response = await fetch(`http://localhost:${port}/actuator/health`);
            const endTime = performance.now();
            const responseTime = Math.round(endTime - startTime);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const data = await response.json();
            return { success: true, data: data, responseTime: responseTime };
        } catch (error) {
            return { success: false, error: error.message };
        }
    }

    async checkHealthViaProxy(port) {
        try {
            const startTime = performance.now();
            // Use NGINX proxy with port as query parameter to avoid CORS
            const response = await fetch(`${this.baseUrl}/actuator/health?port=${port}`);
            const endTime = performance.now();
            const responseTime = Math.round(endTime - startTime);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const data = await response.json();
            return { success: true, data: data, responseTime: responseTime };
        } catch (error) {
            return { success: false, error: error.message };
        }
    }

    async fetchWithTimeout(endpoint, options = {}) {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), this.timeout);

        try {
            const startTime = performance.now();
            const response = await fetch(`${this.baseUrl}${endpoint}`, {
                ...options,
                signal: controller.signal,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });
            clearTimeout(timeoutId);

            const endTime = performance.now();
            const responseTime = Math.round(endTime - startTime);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const data = await response.json();
            
            // Extract instance metadata from headers
            const servedBy = response.headers.get('X-Served-By') || '8080';
            const instance = response.headers.get('X-Instance') || 'unknown';
            const hostname = response.headers.get('X-Hostname') || 'unknown';
            
            // Add response metadata
            return {
                ...data,
                _responseTime: responseTime,
                _status: response.status,
                _timestamp: new Date().toISOString(),
                _servedBy: servedBy,
                _instance: instance,
                _hostname: hostname
            };
        } catch (error) {
            clearTimeout(timeoutId);
            
            if (error.name === 'AbortError') {
                throw new Error('Request timeout');
            }
            
            if (error.message.includes('Failed to fetch')) {
                throw new Error('Network error - unable to connect');
            }
            
            throw error;
        }
    }

    async testConnection() {
        try {
            const response = await this.fetchWithTimeout('/api/students');
            return { success: true, data: response };
        } catch (error) {
            return { success: false, error: error.message };
        }
    }
}

// Create global API client instance
const apiClient = new APIClient();
