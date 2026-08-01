// Chart.js Integration

class ChartManager {
    constructor() {
        this.barChart = null;
        this.pieChart = null;
        this.lineChart = null;
        this.chartColors = {
            8081: 'rgba(99, 102, 241, 0.8)',
            8082: 'rgba(139, 92, 246, 0.8)',
            8083: 'rgba(16, 185, 129, 0.8)',
            border: {
                8081: 'rgba(99, 102, 241, 1)',
                8082: 'rgba(139, 92, 246, 1)',
                8083: 'rgba(16, 185, 129, 1)'
            }
        };
    }

    initBarChart(canvasId) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        this.barChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['8081', '8082', '8083'],
                datasets: [{
                    label: 'Requests',
                    data: [0, 0, 0],
                    backgroundColor: [
                        this.chartColors[8081],
                        this.chartColors[8082],
                        this.chartColors[8083]
                    ],
                    borderColor: [
                        this.chartColors.border[8081],
                        this.chartColors.border[8082],
                        this.chartColors.border[8083]
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            color: '#a0a8c0'
                        },
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        }
                    },
                    x: {
                        ticks: {
                            color: '#a0a8c0'
                        },
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    initPieChart(canvasId) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        this.pieChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['8081', '8082', '8083'],
                datasets: [{
                    data: [1, 1, 1],
                    backgroundColor: [
                        this.chartColors[8081],
                        this.chartColors[8082],
                        this.chartColors[8083]
                    ],
                    borderColor: [
                        this.chartColors.border[8081],
                        this.chartColors.border[8082],
                        this.chartColors.border[8083]
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: '#a0a8c0',
                            padding: 20
                        }
                    }
                }
            }
        });
    }

    initLineChart(canvasId) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        this.lineChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: '8081',
                        data: [],
                        borderColor: this.chartColors.border[8081],
                        backgroundColor: this.chartColors[8081],
                        tension: 0.4,
                        fill: false
                    },
                    {
                        label: '8082',
                        data: [],
                        borderColor: this.chartColors.border[8082],
                        backgroundColor: this.chartColors[8082],
                        tension: 0.4,
                        fill: false
                    },
                    {
                        label: '8083',
                        data: [],
                        borderColor: this.chartColors.border[8083],
                        backgroundColor: this.chartColors[8083],
                        tension: 0.4,
                        fill: false
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'top',
                        labels: {
                            color: '#a0a8c0'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            color: '#a0a8c0'
                        },
                        grid: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        }
                    },
                    x: {
                        ticks: {
                            color: '#a0a8c0'
                        },
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    updateBarChart(data) {
        if (!this.barChart) return;
        
        this.barChart.data.datasets[0].data = [
            data['8081'] || 0,
            data['8082'] || 0,
            data['8083'] || 0
        ];
        this.barChart.update('none');
    }

    updatePieChart(data) {
        if (!this.pieChart) return;
        
        const total = (data['8081'] || 0) + (data['8082'] || 0) + (data['8083'] || 0);
        
        if (total === 0) {
            this.pieChart.data.datasets[0].data = [1, 1, 1];
        } else {
            this.pieChart.data.datasets[0].data = [
                data['8081'] || 0,
                data['8082'] || 0,
                data['8083'] || 0
            ];
        }
        this.pieChart.update('none');
    }

    updateLineChart(timestamp, responseTimes) {
        if (!this.lineChart) return;
        
        const timeLabel = Utils.formatTime(new Date(timestamp));
        
        // Add new data point
        this.lineChart.data.labels.push(timeLabel);
        this.lineChart.data.datasets[0].data.push(responseTimes['8081'] || 0);
        this.lineChart.data.datasets[1].data.push(responseTimes['8082'] || 0);
        this.lineChart.data.datasets[2].data.push(responseTimes['8083'] || 0);
        
        // Keep only last 20 data points
        if (this.lineChart.data.labels.length > 20) {
            this.lineChart.data.labels.shift();
            this.lineChart.data.datasets.forEach(dataset => {
                dataset.data.shift();
            });
        }
        
        this.lineChart.update('none');
    }

    resetCharts() {
        if (this.barChart) {
            this.barChart.data.datasets[0].data = [0, 0, 0];
            this.barChart.update();
        }
        
        if (this.pieChart) {
            this.pieChart.data.datasets[0].data = [1, 1, 1];
            this.pieChart.update();
        }
        
        if (this.lineChart) {
            this.lineChart.data.labels = [];
            this.lineChart.data.datasets.forEach(dataset => {
                dataset.data = [];
            });
            this.lineChart.update();
        }
    }

    destroyCharts() {
        if (this.barChart) {
            this.barChart.destroy();
            this.barChart = null;
        }
        
        if (this.pieChart) {
            this.pieChart.destroy();
            this.pieChart = null;
        }
        
        if (this.lineChart) {
            this.lineChart.destroy();
            this.lineChart = null;
        }
    }
}

// Create global chart manager instance
const chartManager = new ChartManager();
