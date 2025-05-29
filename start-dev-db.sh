#!/bin/bash

# Make the script executable: chmod +x start-dev-db.sh

set -e  # Exit immediately if any command fails

echo "🐳 Starting Development database..."
docker compose --profile dev up database --build