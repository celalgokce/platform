@echo off
echo 🐳 Starting HealthVia Development Environment...

echo 📦 Starting MongoDB...
docker-compose up -d

echo ⏳ Waiting for MongoDB to be ready...
timeout /t 10 /nobreak

echo 🚀 Starting Spring Boot Application...
mvnw.cmd spring-boot:run

echo ✅ HealthVia Platform is running!
echo 🌐 Application: http://localhost:8080
echo 🗄️  MongoDB: localhost:27017