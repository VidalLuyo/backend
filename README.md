# Microservice for Assistance
## 🔧 Project Stack

- **Backend**: Java 17 + Spring Boot WebFlux
- **Frontend**: Angular
- **Database**: PostgreSQL
- **Contenedores**: Docker

---

## ✅ Project Purpose

This SIGEI project at our Capilla de Asia school aims to improve attendance tracking at the school
---

## 🛠️ Setup Instructions

1. **Clone** the repository:
   ```bash
   git clone https://github.com/YourOrg/cetpro-social-project.git
   cd cetpro-social-project
   ```

2. **Run** with Maven:
   ```bash
   mvn spring-boot:run
   ```

3. **Or run** with Docker:
   ```bash
   docker-compose up -d
   ```

---

## 🧩 How to Use

- You **should** open `http://localhost:9087` after the backend is running.
- You **should** access Swagger UI at `http://localhost:9087/swagger-ui.html` to test the API.
- You **should** check health at `http://localhost:9087/actuator/health`.

---

## 📁 Repository Structure

```text
/cetpro-social-project
├── src/main/java/          # Código Java
├── src/main/resources/     # Configuración
├── pom.xml                 # Dependencias
├── Dockerfile              # Imagen Docker
├── docker-compose.yml      # Microservicios
└── README.md               # ← You are here
```

---

## 🧑‍🏫 Contributing

- **Fork** this repo.
- **Create** a feature branch: `git checkout -b feature/your-feature`
- **Implement** and **test** your feature.
- **Open** a Pull Request.

---

## 🚀 Deployment

You **must** set these environment variables:
- `SERVER_PORT=9087`
- `MS_INSTITUTION=http://localhost:9080`
- `MS_STUDENT=http://localhost:9081`

---

---

## 🧩 How to Use the API (Advice with "should")

- You **should** access the Swagger UI at `http://localhost:9087/swagger-ui.html` to explore all available endpoints.
- You **should** test the API using the interactive Swagger documentation before integrating with frontend applications.
- You **should** verify the PostgreSQL connection before performing any operations.

---

## 🎯 Future Plans (Advice & Suggestions)

- We **should** implement JWT authentication and authorization before production deployment.
- We **should** add Redis caching layer to improve read performance for frequently accessed data.
- We **should** implement event-driven communication using Apache Kafka or RabbitMQ.

---

