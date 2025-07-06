#!/bin/bash
echo "🐳 Starting HealthVia Development Environment..."

# Docker MongoDB'yi başlat
echo "📦 Starting MongoDB..."
docker-compose up -d

# MongoDB'nin hazır olmasını bekle
echo "⏳ Waiting for MongoDB to be ready..."
sleep 10

# Spring Boot uygulamasını başlat
echo "🚀 Starting Spring Boot Application..."
./mvnw spring-boot:run

echo "✅ HealthVia Platform is running!"
echo "🌐 Application: http://localhost:8080"
echo "🗄️  MongoDB: localhost:27017"