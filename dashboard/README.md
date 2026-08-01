# Load Balancer Dashboard

A professional web dashboard for monitoring and testing the Distributed Student Result Management System with NGINX load balancing.

## Features

- **Modern Dark UI** with glassmorphism design
- **Real-time Monitoring** of Spring Boot instances
- **Load Balancing Visualization** showing request distribution
- **Single Request Testing** with detailed response analysis
- **Bulk Request Testing** with performance metrics
- **Live Request Logs** with export functionality
- **Interactive Charts** using Chart.js
- **Health Monitoring** for all instances
- **Architecture Visualization** with animated request flow
- **Connection Settings** for configurable API endpoints

## Quick Start

### Option 1: Open Directly in Browser

Simply double-click `index.html` to open it in your browser.

### Option 2: Use Local Server

```bash
cd "c:\Users\sivas\Desktop\distubted server equally\dashboard"
python -m http.server 8080
```

Then open: http://localhost:8080

## Prerequisites

Ensure your backend is running:

1. **NGINX Load Balancer** on http://localhost
2. **Spring Boot Instances** on ports 8081, 8082, 8083
3. **Redis** on port 6379
4. **MySQL** on port 3306

## Usage

### Dashboard Overview

- **Performance Cards**: Show completed requests, failures, average time, fastest, slowest, and RPS
- **Port Distribution**: Real-time count of requests handled by each instance
- **Charts**: Bar chart for request distribution, pie chart for percentages, line chart for response times
- **Architecture Diagram**: Animated visualization of request flow through the system
- **Health Monitor**: Real-time health status of all instances

### Single Request Test

1. Navigate to "Single Test" section
2. Click "Send Request" button
3. View detailed response including:
   - JSON response
   - Served by instance
   - Response time
   - HTTP status

### Bulk Request Test

1. Navigate to "Bulk Test" section
2. Enter number of requests or use preset buttons (10, 50, 100, 500, 1000)
3. Click "Run Test"
4. Monitor progress bar and ETA
5. View results including:
   - Completed/Failed count
   - Average/Min/Max response times

### Logs

1. Navigate to "Logs" section
2. View real-time request logs
3. Clear logs or export to CSV/JSON

### Analytics

1. Navigate to "Analytics" section
2. View response times over time chart

### Settings

1. Navigate to "Settings" section
2. Change base URL (default: http://localhost)
3. Toggle auto-refresh health monitor

## File Structure

```
dashboard/
├── index.html      # Main HTML structure
├── style.css       # Styling with glassmorphism
├── script.js       # Main application logic
├── charts.js       # Chart.js integration
├── utils.js        # Utility functions
├── api.js          # API client
└── README.md       # This file
```

## Technical Details

### Technologies Used

- **HTML5** - Structure
- **CSS3** - Styling with glassmorphism effects
- **Vanilla JavaScript** - No frameworks
- **Chart.js 4.4.0** - Charts and visualizations

### Browser Compatibility

- Chrome/Edge (recommended)
- Firefox
- Safari
- Modern browsers with ES6+ support

### API Endpoints

The dashboard connects to:

- `GET /api/students` - Main endpoint for testing
- `GET /actuator/health` - Health check endpoint

### Features Implementation

- **Connection Status**: Auto-checks connection on load
- **Load Balancing Detection**: Parses response headers to identify serving instance
- **Real-time Updates**: Charts and stats update automatically
- **Error Handling**: Graceful handling of network errors, timeouts, and HTTP errors
- **Responsive Design**: Works on desktop and mobile devices

## Customization

### Change Default Base URL

Edit the default in `script.js` or use the Settings section in the dashboard.

### Modify Chart Colors

Edit `chartColors` in `charts.js`.

### Adjust Health Check Interval

Edit the interval in `script.js` (default: 5000ms).

## Troubleshooting

### Dashboard not connecting

1. Verify backend is running
2. Check base URL in Settings
3. Ensure CORS is enabled on backend
4. Check browser console for errors

### Charts not displaying

1. Verify Chart.js CDN is accessible
2. Check browser console for errors
3. Ensure canvas elements exist in HTML

### Health checks failing

1. Verify instances are running on correct ports
2. Check firewall settings
3. Ensure actuator endpoints are enabled

## Performance

- **Lightweight**: No frameworks, pure vanilla JS
- **Fast**: Minimal DOM manipulation
- **Efficient**: Debounced and throttled operations
- **Scalable**: Handles 1000+ concurrent requests

## Security

- No sensitive data stored
- All API calls use HTTPS if base URL uses HTTPS
- No authentication required (adjust as needed for production)

## License

This dashboard is part of the Distributed Student Result Management System.

## Support

For issues or questions, refer to the main project documentation.
