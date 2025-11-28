# CETPRO Social Project – Microservicio de Asistencias

## 🔧 Project Stack

- **Backend**: Java 17 + Spring Boot WebFlux
- **Frontend**: Angular
- **Database**: PostgreSQL
- **Contenedores**: Docker

---

## ✅ Project Purpose

This CSR initiative by our CETPRO institute delivers **free technical tutorials** and **community support** using real-world technology tools, empowering learners to build and maintain their own computer systems.

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

## 💡 Best Practices

- You **should** run `mvn test` before each commit.
- You **should** document new endpoints in Swagger.

---

## 📞 Support

- **Open** an issue in this repository.
- **Tag** `@project-lead` for urgent issues.

---

