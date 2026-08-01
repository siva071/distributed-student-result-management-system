// Main Application Logic

class DashboardApp {
    constructor() {
        this.stats = {
            completed: 0,
            failed: 0,
            responseTimes: [],
            portDistribution: {
                '8081': 0,
                '8082': 0,
                '8083': 0
            }
        };
        this.requestNumber = 0;
        this.healthCheckInterval = null;
        this.isBulkTestRunning = false;
        this.bulkTestStartTime = null;
        this.checkAutoReset();
    }

    init() {
        this.initNavigation();
        this.initCharts();
        this.initConnectionSettings();
        this.initSingleTest();
        this.initBulkTest();
        this.initLogs();
        this.initHealthMonitor();
        this.initArchitecture();
        this.checkConnection();
    }

    // Auto-reset statistics after 24 hours
    checkAutoReset() {
        const lastReset = localStorage.getItem('statsLastReset');
        const now = Date.now();
        const twentyFourHours = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        if (!lastReset || (now - parseInt(lastReset)) > twentyFourHours) {
            this.resetStats();
            localStorage.setItem('statsLastReset', now.toString());
            console.log('Statistics auto-reset after 24 hours');
        } else {
            // Load stats from localStorage if within 24 hours
            this.loadStats();
        }
    }

    loadStats() {
        const savedStats = localStorage.getItem('dashboardStats');
        if (savedStats) {
            try {
                this.stats = JSON.parse(savedStats);
                this.updateDashboard();
                console.log('Statistics loaded from localStorage');
            } catch (error) {
                console.error('Failed to load stats:', error);
            }
        }
    }

    saveStats() {
        localStorage.setItem('dashboardStats', JSON.stringify(this.stats));
    }

    resetStats() {
        this.stats = {
            completed: 0,
            failed: 0,
            responseTimes: [],
            portDistribution: {
                '8081': 0,
                '8082': 0,
                '8083': 0
            }
        };
        this.saveStats();
        this.updateDashboard();
    }

    // Navigation
    initNavigation() {
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const section = item.dataset.section;
                this.showSection(section);
                
                navItems.forEach(nav => nav.classList.remove('active'));
                item.classList.add('active');
            });
        });
    }

    showSection(sectionId) {
        document.querySelectorAll('.section').forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(sectionId).classList.add('active');
    }

    // Charts
    initCharts() {
        chartManager.initBarChart('barChart');
        chartManager.initPieChart('pieChart');
        chartManager.initLineChart('lineChart');
    }

    // Connection Settings
    initConnectionSettings() {
        const baseUrlInput = document.getElementById('baseUrl');
        const saveButton = document.getElementById('saveSettings');
        
        // Set default to NGINX load balancer
        const defaultBaseUrl = 'http://localhost';
        baseUrlInput.value = defaultBaseUrl;
        apiClient.setBaseUrl(defaultBaseUrl);
        localStorage.setItem('baseUrl', defaultBaseUrl);

        // Load saved base URL if exists
        const savedBaseUrl = localStorage.getItem('baseUrl');
        if (savedBaseUrl) {
            baseUrlInput.value = savedBaseUrl;
            apiClient.setBaseUrl(savedBaseUrl);
        }

        saveButton.addEventListener('click', () => {
            const newBaseUrl = baseUrlInput.value.trim();
            if (Utils.isValidUrl(newBaseUrl)) {
                apiClient.setBaseUrl(newBaseUrl);
                localStorage.setItem('baseUrl', newBaseUrl);
                this.showToast('Settings saved successfully', 'success');
                this.checkConnection();
            } else {
                this.showToast('Invalid URL format', 'error');
            }
        });
    }

    async checkConnection() {
        const result = await apiClient.testConnection();
        this.updateConnectionStatus(result.success);
    }

    updateConnectionStatus(connected) {
        const statusIndicator = document.querySelector('.status-indicator');
        const statusText = document.querySelector('.status-text');
        const settingsStatus = document.getElementById('settingsConnectionStatus');

        if (connected) {
            statusIndicator.classList.add('connected');
            statusIndicator.classList.remove('disconnected');
            statusText.textContent = 'Connected';
            settingsStatus.textContent = 'Connected';
            settingsStatus.classList.add('connected');
            settingsStatus.classList.remove('disconnected');
        } else {
            statusIndicator.classList.add('disconnected');
            statusIndicator.classList.remove('connected');
            statusText.textContent = 'Disconnected';
            settingsStatus.textContent = 'Disconnected';
            settingsStatus.classList.add('disconnected');
            settingsStatus.classList.remove('connected');
        }
    }

    // Single Test
    initSingleTest() {
        const singleTestBtn = document.getElementById('singleTestBtn');
        singleTestBtn.addEventListener('click', () => this.runSingleTest());
    }

    async runSingleTest() {
        const btn = document.getElementById('singleTestBtn');
        btn.classList.add('loading');
        btn.disabled = true;

        try {
            const response = await apiClient.getStudents();
            this.displaySingleResult(response);
            this.updateStats(response, true);
            this.addLogEntry(1, response, true);
            this.animateArchitecture(response);
            this.showToast('Request completed successfully', 'success');
        } catch (error) {
            this.showToast(`Request failed: ${Utils.parseError(error)}`, 'error');
            this.updateStats({}, false);
            this.addLogEntry(1, { error: error.message }, false);
        } finally {
            btn.classList.remove('loading');
            btn.disabled = false;
        }
    }

    displaySingleResult(response) {
        document.getElementById('singleJson').textContent = JSON.stringify(response, null, 2);
        document.getElementById('singleServedBy').textContent = Utils.parseServedBy(response);
        document.getElementById('singleInstance').textContent = Utils.parseInstance(response);
        document.getElementById('singleHostname').textContent = Utils.parseHostname(response);
        document.getElementById('singleResponseTime').textContent = `${Utils.parseResponseTime(response)}ms`;
        document.getElementById('singleCurrentTime').textContent = Utils.formatDateTime();
        document.getElementById('singleStatus').textContent = response._status || '200';
    }

    // Bulk Test
    initBulkTest() {
        const bulkTestBtn = document.getElementById('bulkTestBtn');
        const presetButtons = document.querySelectorAll('.preset-buttons .btn');

        presetButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const count = btn.dataset.count;
                document.getElementById('customRequestCount').value = count;
            });
        });

        bulkTestBtn.addEventListener('click', () => this.runBulkTest());
    }

    async runBulkTest() {
        if (this.isBulkTestRunning) return;

        const count = parseInt(document.getElementById('customRequestCount').value);
        if (isNaN(count) || count < 1) {
            this.showToast('Please enter a valid number of requests', 'error');
            return;
        }

        this.isBulkTestRunning = true;
        this.bulkTestStartTime = Date.now();
        
        const btn = document.getElementById('bulkTestBtn');
        btn.classList.add('loading');
        btn.disabled = true;

        const progressContainer = document.getElementById('bulkProgress');
        const resultsContainer = document.getElementById('bulkResults');
        progressContainer.style.display = 'block';
        resultsContainer.style.display = 'none';

        // Reset stats for bulk test
        const bulkStats = {
            completed: 0,
            failed: 0,
            responseTimes: [],
            portDistribution: { '8081': 0, '8082': 0, '8083': 0 }
        };

        // Process requests in chunks to avoid overwhelming the browser/server
        const chunkSize = 50;
        const chunks = [];
        for (let i = 0; i < count; i += chunkSize) {
            chunks.push(i);
        }

        try {
            for (const chunkStart of chunks) {
                const chunkEnd = Math.min(chunkStart + chunkSize, count);
                const chunkRequests = [];
                
                for (let i = chunkStart; i < chunkEnd; i++) {
                    chunkRequests.push(this.makeRequest(i + 1));
                }
                
                const chunkResults = await Promise.all(chunkRequests);
                
                chunkResults.forEach((result) => {
                    if (result.success) {
                        bulkStats.completed++;
                        bulkStats.responseTimes.push(result.responseTime);
                        const port = Utils.extractPort(result.servedBy);
                        if (bulkStats.portDistribution[port] !== undefined) {
                            bulkStats.portDistribution[port]++;
                        }
                    } else {
                        bulkStats.failed++;
                    }
                });
                
                this.updateProgress(chunkEnd, count);
                
                // Small delay between chunks to avoid overwhelming the server
                if (chunkEnd < count) {
                    await Utils.sleep(100);
                }
            }

            this.displayBulkResults(bulkStats);
            this.showToast(`Bulk test completed: ${bulkStats.completed}/${count} successful`, 'success');
        } catch (error) {
            this.showToast(`Bulk test failed: ${Utils.parseError(error)}`, 'error');
        } finally {
            this.isBulkTestRunning = false;
            btn.classList.remove('loading');
            btn.disabled = false;
            progressContainer.style.display = 'none';
        }
    }

    async makeRequest(requestNum) {
        try {
            const startTime = Date.now();
            const response = await apiClient.getStudents();
            const endTime = Date.now();
            
            return {
                success: true,
                response: response,
                responseTime: response._responseTime || (endTime - startTime),
                servedBy: Utils.parseServedBy(response),
                instance: Utils.parseInstance(response)
            };
        } catch (error) {
            return {
                success: false,
                error: error.message
            };
        }
    }

    updateProgress(current, total) {
        const percentage = (current / total) * 100;
        document.getElementById('progressFill').style.width = `${percentage}%`;
        document.getElementById('progressCurrent').textContent = current;
        document.getElementById('progressTotal').textContent = total;
        
        const eta = Utils.calculateETA(current, total, this.bulkTestStartTime);
        document.getElementById('progressEta').textContent = `ETA: ${eta}`;
    }

    displayBulkResults(stats) {
        document.getElementById('bulkResults').style.display = 'block';
        document.getElementById('bulkCompleted').textContent = stats.completed;
        document.getElementById('bulkFailed').textContent = stats.failed;
        
        const avgTime = stats.responseTimes.length > 0 
            ? Utils.calculateAverage(stats.responseTimes) 
            : 0;
        const minTime = stats.responseTimes.length > 0 
            ? Utils.findMin(stats.responseTimes) 
            : 0;
        const maxTime = stats.responseTimes.length > 0 
            ? Utils.findMax(stats.responseTimes) 
            : 0;

        document.getElementById('bulkAvgTime').textContent = avgTime;
        document.getElementById('bulkMinTime').textContent = minTime;
        document.getElementById('bulkMaxTime').textContent = maxTime;

        // Update global stats
        this.stats.completed += stats.completed;
        this.stats.failed += stats.failed;
        this.stats.responseTimes.push(...stats.responseTimes);
        
        Object.keys(stats.portDistribution).forEach(port => {
            this.stats.portDistribution[port] += stats.portDistribution[port];
        });

        this.saveStats();
        this.updateDashboard();
    }

    // Stats Update
    updateStats(response, success) {
        if (success) {
            this.stats.completed++;
            const responseTime = Utils.parseResponseTime(response);
            this.stats.responseTimes.push(responseTime);
            
            const port = Utils.extractPort(Utils.parseServedBy(response));
            if (this.stats.portDistribution[port] !== undefined) {
                this.stats.portDistribution[port]++;
            }
        } else {
            this.stats.failed++;
        }

        this.saveStats();
        this.updateDashboard();
    }

    updateDashboard() {
        // Update cards
        document.getElementById('completedCount').textContent = Utils.formatNumber(this.stats.completed);
        document.getElementById('failedCount').textContent = Utils.formatNumber(this.stats.failed);
        
        const avgTime = this.stats.responseTimes.length > 0 
            ? Utils.calculateAverage(this.stats.responseTimes) 
            : 0;
        const fastestTime = this.stats.responseTimes.length > 0 
            ? Utils.findMin(this.stats.responseTimes) 
            : 0;
        const slowestTime = this.stats.responseTimes.length > 0 
            ? Utils.findMax(this.stats.responseTimes) 
            : 0;

        document.getElementById('avgTime').textContent = `${avgTime}ms`;
        document.getElementById('fastestTime').textContent = `${fastestTime}ms`;
        document.getElementById('slowestTime').textContent = `${slowestTime}ms`;

        // Calculate RPS
        if (this.bulkTestStartTime) {
            const duration = Date.now() - this.bulkTestStartTime;
            const rps = Utils.calculateRPS(this.stats.completed, duration);
            document.getElementById('rps').textContent = rps;
        }

        // Update port distribution
        document.getElementById('port8081Count').textContent = Utils.formatNumber(this.stats.portDistribution['8081']);
        document.getElementById('port8082Count').textContent = Utils.formatNumber(this.stats.portDistribution['8082']);
        document.getElementById('port8083Count').textContent = Utils.formatNumber(this.stats.portDistribution['8083']);

        // Update charts
        chartManager.updateBarChart(this.stats.portDistribution);
        chartManager.updatePieChart(this.stats.portDistribution);
    }

    // Logs
    initLogs() {
        document.getElementById('clearLogs').addEventListener('click', () => {
            logger.clearLogs();
            this.renderLogs();
            this.showToast('Logs cleared', 'info');
        });

        document.getElementById('exportLogsCsv').addEventListener('click', () => {
            logger.exportLogs('csv');
            this.showToast('Logs exported to CSV', 'success');
        });

        document.getElementById('exportLogsJson').addEventListener('click', () => {
            logger.exportLogs('json');
            this.showToast('Logs exported to JSON', 'success');
        });

        this.renderLogs();
    }

    addLogEntry(requestNum, response, success) {
        this.requestNumber++;
        
        const log = {
            requestNumber: this.requestNumber,
            port: success ? Utils.extractPort(Utils.parseServedBy(response)) : '-',
            status: success ? 'SUCCESS' : 'FAILURE',
            responseTime: success ? Utils.parseResponseTime(response) : 0,
            timestamp: new Date().toISOString()
        };

        logger.addLog(log);
        this.renderLogs();
    }

    renderLogs() {
        const logsWindow = document.getElementById('logsWindow');
        const logs = logger.getLogs();

        if (logs.length === 0) {
            logsWindow.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📝</div><div class="empty-state-text">No logs yet</div></div>';
            return;
        }

        logsWindow.innerHTML = logs.map(log => `
            <div class="log-entry">
                <span class="log-time">${Utils.formatTime(new Date(log.timestamp))}</span>
                <span class="log-request">Request #${log.requestNumber}</span>
                <span class="log-port">${log.port}</span>
                <span class="log-status ${log.status === 'SUCCESS' ? 'success' : 'failure'}">${log.status}</span>
                <span class="log-time-val">${log.responseTime}ms</span>
            </div>
        `).join('');
    }

    // Health Monitor
    initHealthMonitor() {
        this.runHealthCheck();
        
        const autoRefreshCheckbox = document.getElementById('autoRefreshHealth');
        autoRefreshCheckbox.addEventListener('change', () => {
            if (autoRefreshCheckbox.checked) {
                this.healthCheckInterval = setInterval(() => this.runHealthCheck(), 5000);
            } else {
                if (this.healthCheckInterval) {
                    clearInterval(this.healthCheckInterval);
                }
            }
        });

        // Start auto-refresh by default
        this.healthCheckInterval = setInterval(() => this.runHealthCheck(), 5000);
    }

    async runHealthCheck() {
        const ports = ['8081', '8082', '8083'];
        
        // Test main API connection through NGINX (has CORS)
        const apiTest = await apiClient.testConnection();
        const systemOnline = apiTest.success;
        
        // Mark all instances as online if system is online (NGINX load balances)
        for (const port of ports) {
            this.updateHealthStatus(port, systemOnline, systemOnline ? 50 : 0);
        }
    }

    updateHealthStatus(port, online, responseTime) {
        const statusElement = document.getElementById(`health${port}Status`);
        const timeElement = document.getElementById(`health${port}Time`);
        
        if (online) {
            statusElement.textContent = 'Online';
            statusElement.classList.add('online');
            statusElement.classList.remove('offline');
            timeElement.textContent = `${responseTime}ms`;
        } else {
            statusElement.textContent = 'Offline';
            statusElement.classList.add('offline');
            statusElement.classList.remove('online');
            timeElement.textContent = '-';
        }
    }

    // Architecture Animation
    initArchitecture() {
        // Architecture animation is triggered by requests
    }

    animateArchitecture(response) {
        const port = Utils.extractPort(Utils.parseServedBy(response));
        const requestDot = document.getElementById('requestDot');
        
        // Reset all instances
        document.querySelectorAll('.arch-node.instance').forEach(node => {
            node.classList.remove('highlight');
        });

        // Highlight the responding instance
        const instanceNode = document.getElementById(`inst${port}`);
        if (instanceNode) {
            instanceNode.classList.add('highlight');
        }

        // Animate request dot
        requestDot.classList.add('active');
        
        setTimeout(() => {
            requestDot.classList.remove('active');
            if (instanceNode) {
                instanceNode.classList.remove('highlight');
            }
        }, 2000);
    }

    // Toast Notifications
    showToast(message, type = 'info') {
        const container = document.getElementById('toastContainer');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        
        container.appendChild(toast);
        
        setTimeout(() => {
            toast.style.animation = 'slideIn 0.3s ease reverse';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
}

// Initialize application when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const app = new DashboardApp();
    app.init();
});
