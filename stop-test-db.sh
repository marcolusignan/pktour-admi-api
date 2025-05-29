#!/bin/bash

# Make the script executable: chmod +x stop-test-db.sh

set -e  # Exit immediately if any command fails

echo "🐳 Stopping Test database..."
docker compose --profile test down