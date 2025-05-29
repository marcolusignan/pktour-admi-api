#!/bin/bash

# Make the script executable: chmod +x start-test-db.sh

set -e  # Exit immediately if any command fails

echo "🐳 Starting Test database..."
docker compose --profile test up --build