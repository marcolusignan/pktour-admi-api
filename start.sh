#!/bin/bash

# Make the script executable: chmod +x start.sh

set -e  # Exit immediately if any command fails

# Check for --docker argument
USE_DOCKER=false
for arg in "$@"; do
  if [ "$arg" == "--docker" ]; then
    USE_DOCKER=true
    break
  fi
done

echo "🔨 Building Ktor fat JAR..."
./gradlew buildFatJar

if [ "$USE_DOCKER" = true ]; then
  echo "🐳 Running with Docker Compose..."
  docker compose --profile dev up --build
else
  echo "🚀 Starting Application from fat JAR..."
  java -jar build/libs/pktouradmin-all.jar
fi
