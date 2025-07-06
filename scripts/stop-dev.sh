#!/bin/bash
echo "🛑 Stopping HealthVia Development Environment..."

# Spring Boot'u durdur (Ctrl+C ile manuel)
echo "⚠️  Press Ctrl+C to stop Spring Boot if running"

# MongoDB'yi durdur
echo "📦 Stopping MongoDB..."
docker-compose down

echo "✅ Environment stopped!"