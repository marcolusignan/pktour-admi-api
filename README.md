# 🃏 PKTourAdmin API

**PKTourAdmin** is a Kotlin-based REST API tailored for managing poker tournaments with precision and ease.

---

## 🚀 Features

### 🛠️ Functional Highlights

* 🎯 Create a player
* 🔄 Update a player's score
* 🔍 Retrieve player data by ID
* 🏆 List all players ordered by **dense ranking**
* 🧹 Delete all players

### ⚙️ Technical Highlights

* Built with [**Ktor**](https://ktor.io/docs), a powerful Kotlin framework for building asynchronous applications
* 🔐 Implements **Basic Authentication** for secure access
* 🐳 **Docker-ready** for seamless deployment
* 📘 Offers **OpenAPI documentation** at the `/openapi` endpoint
* 🧪 Comprehensive **integration testing** with Ktor's test client

---

## 📋 Prerequisites

Make sure the following tools are installed before running the application:

* **JDK 22**
* **Docker** (optional, for containerized setup)
* An active **MongoDB** instance

---

## 🏃 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/marcolusignan/pktour-admi-api
cd pktouradmin
```

### 2. Set up environment variables

Export environment variables directly or create a `.env` file based on `.env.template` at the project root:

```bash
export API_LOGIN=...
export API_PWD=...
export MONGO_USER=...
export MONGO_PWD=...
```

### 3. Run the application

To start the application (use `--docker` for Docker deployment):

```bash
./start.sh
```

### 4. Launch a local Docker database (optional)

```bash
./start-dev-db.sh
```

### 5. Stop Docker services

```bash
./stop-docker.sh
```

---

## 🧪 Running Integration Tests

### 1. Execute tests

```bash
./gradlew test
```

### 2. Use Docker for the test database (optional)

```bash
# Start test DB
./start-test-db.sh

# Stop test DB
./stop-test-db.sh
```

---

## 📎 Notes

* The OpenAPI spec is available at: `http://localhost:<port>/openapi`
* Default port and credentials can be configured via environment variables

