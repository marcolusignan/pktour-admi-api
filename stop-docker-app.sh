#!/bin/bash

# Make the script executable: chmod +x stop-docker.sh

echo "🐳 Stopping Application..."
docker compose --profile dev down
