// Utility Functions

class Utils {
    // Format time to HH:MM:SS
    static formatTime(date = new Date()) {
        return date.toLocaleTimeString('en-US', {
            hour12: false,
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    // Format date to YYYY-MM-DD HH:MM:SS
    static formatDateTime(date = new Date()) {
        return date.toISOString().replace('T', ' ').substring(0, 19);
    }

    // Format milliseconds to readable time
    static formatDuration(ms) {
        if (ms < 1000) return `${ms}ms`;
        if (ms < 60000) return `${(ms / 1000).toFixed(2)}s`;
        return `${(ms / 60000).toFixed(2)}m`;
    }

    // Calculate average from array of numbers
    static calculateAverage(numbers) {
        if (numbers.length === 0) return 0;
        const sum = numbers.reduce((a, b) => a + b, 0);
        return Math.round(sum / numbers.length);
    }

    // Find minimum in array
    static findMin(numbers) {
        if (numbers.length === 0) return 0;
        return Math.min(...numbers);
    }

    // Find maximum in array
    static findMax(numbers) {
        if (numbers.length === 0) return 0;
        return Math.max(...numbers);
    }

    // Calculate requests per second
    static calculateRPS(totalRequests, durationMs) {
        if (durationMs === 0) return 0;
        return Math.round((totalRequests / durationMs) * 1000);
    }

    // Format number with commas
    static formatNumber(num) {
        return num.toLocaleString();
    }

    // Generate unique ID
    static generateId() {
        return Date.now().toString(36) + Math.random().toString(36).substr(2);
    }

    // Debounce function
    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // Throttle function
    static throttle(func, limit) {
        let inThrottle;
        return function(...args) {
            if (!inThrottle) {
                func.apply(this, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    }

    // Deep clone object
    static deepClone(obj) {
        return JSON.parse(JSON.stringify(obj));
    }

    // Check if object is empty
    static isEmpty(obj) {
        return Object.keys(obj).length === 0;
    }

    // Sleep function for async operations
    static sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // Parse servedBy from response
    static parseServedBy(response) {
        return response.servedBy || response._servedBy || 'Unknown';
    }

    // Parse instance from response
    static parseInstance(response) {
        return response.instance || response._instance || 'Unknown';
    }

    // Parse hostname from response
    static parseHostname(response) {
        return response.hostname || response._hostname || 'Unknown';
    }

    // Parse response time from response
    static parseResponseTime(response) {
        return response.responseTime || response._responseTime || 0;
    }

    // Extract port from servedBy string
    static extractPort(servedBy) {
        const match = servedBy.match(/\d{4}/);
        return match ? match[0] : 'Unknown';
    }

    // Export data to CSV
    static exportToCSV(data, filename) {
        if (!data || data.length === 0) {
            console.error('No data to export');
            return;
        }

        const headers = Object.keys(data[0]);
        const csvContent = [
            headers.join(','),
            ...data.map(row => headers.map(header => {
                const value = row[header];
                const stringValue = typeof value === 'object' ? JSON.stringify(value) : String(value);
                return `"${stringValue.replace(/"/g, '""')}"`;
            }).join(','))
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        link.setAttribute('href', url);
        link.setAttribute('download', `${filename}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    // Export data to JSON
    static exportToJSON(data, filename) {
        if (!data || data.length === 0) {
            console.error('No data to export');
            return;
        }

        const jsonContent = JSON.stringify(data, null, 2);
        const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        link.setAttribute('href', url);
        link.setAttribute('download', `${filename}.json`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    // Calculate ETA based on progress
    static calculateETA(current, total, startTime) {
        if (current === 0 || total === 0) return '--';
        
        const elapsed = Date.now() - startTime;
        const rate = current / elapsed;
        const remaining = total - current;
        const eta = remaining / rate;
        
        if (eta < 1000) return `${Math.round(eta)}ms`;
        if (eta < 60000) return `${Math.round(eta / 1000)}s`;
        return `${Math.round(eta / 60000)}m`;
    }

    // Validate URL
    static isValidUrl(string) {
        try {
            new URL(string);
            return true;
        } catch (_) {
            return false;
        }
    }

    // Get color based on status
    static getStatusColor(status) {
        switch (status) {
            case 'success':
            case 'online':
                return '#10b981';
            case 'error':
            case 'failure':
            case 'offline':
                return '#ef4444';
            case 'warning':
                return '#f59e0b';
            default:
                return '#6366f1';
        }
    }

    // Truncate text
    static truncate(text, length = 50) {
        if (text.length <= length) return text;
        return text.substring(0, length) + '...';
    }

    // Parse error message
    static parseError(error) {
        if (typeof error === 'string') return error;
        if (error.message) return error.message;
        if (error.statusText) return error.statusText;
        return 'Unknown error';
    }
}

// Logger class for request logging
class Logger {
    constructor() {
        this.logs = [];
        this.maxLogs = 1000;
    }

    addLog(log) {
        const logEntry = {
            id: Utils.generateId(),
            timestamp: new Date(),
            time: Utils.formatTime(),
            ...log
        };

        this.logs.unshift(logEntry);
        
        // Keep only maxLogs
        if (this.logs.length > this.maxLogs) {
            this.logs = this.logs.slice(0, this.maxLogs);
        }

        return logEntry;
    }

    getLogs() {
        return this.logs;
    }

    clearLogs() {
        this.logs = [];
    }

    getLogsCount() {
        return this.logs.length;
    }

    exportLogs(format = 'json') {
        if (format === 'csv') {
            Utils.exportToCSV(this.logs, 'request_logs');
        } else {
            Utils.exportToJSON(this.logs, 'request_logs');
        }
    }
}

// Create global logger instance
const logger = new Logger();
