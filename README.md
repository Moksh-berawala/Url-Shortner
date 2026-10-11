# URL Shortener

A web-based URL shortener built with **Java, Spring Boot, Spring Security, Thymeleaf, and PostgreSQL**. It lets users create shorter links, manage their URLs, and redirect visitors to the original destinations.

## Features

- **URL shortening** — create a short key for a longer URL.
- **User accounts** — register and log in.
- **URL management** — view and manage your own short URLs.
- **Public and private URLs** — control whether a short URL can be accessed by other users.
- **Expiration dates** — short URLs can expire.
- **Click tracking** — track visits to short URLs.
- **Pagination** — browse URL listings in pages.
- **Admin dashboard** — administrative views for users with the admin role.
- **Database migrations** — Flyway manages versioned database schema/data migrations.
- **Server-rendered UI** — Thymeleaf templates with Bootstrap.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | Application framework |
| Spring MVC | Web controllers and routing |
| Spring Security | Authentication and authorization |
| Spring Data JPA / Hibernate | Persistence and ORM |
| PostgreSQL | Relational database |
| Flyway | Database migrations |
| Thymeleaf | Server-side HTML templates |
| Bootstrap 5 | UI styling |
| Maven | Build and dependency management |

## Prerequisites

- JDK 21 or compatible JDK
- PostgreSQL
- Git (to clone the repository)

The project includes the Maven Wrapper, so a separate Maven installation is not required.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Moksh-berawala/Url-Shortner.git
cd Url-Shortner
```

### 2. Create a PostgreSQL database

Create a database named `Url-Shortener` in PostgreSQL, or choose another database name and update the application configuration accordingly.

### 3. Configure the application

Open:

```text
src/main/resources/application.properties
```

Configure the database connection and credentials for your local PostgreSQL installation. **Do not commit real passwords or other secrets.** Prefer environment variables for credentials, for example:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/Url-Shortener}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

server.port=${SERVER_PORT:8081}
app.base-url=${APP_BASE_URL:http://localhost:8081}
```

Set `SPRING_DATASOURCE_PASSWORD` in your IDE run configuration or shell environment before starting the app. If you use additional security-related environment variables, configure those in the same way.

> The repository's current configuration may differ from this example. Review `application.properties` and remove any hardcoded credentials before publishing or sharing the project.

### 4. Run the application

**Windows PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
./mvnw spring-boot:run
```

By default, the application is configured to use port `8081`.

Open:

```text
http://localhost:8081
```

### 5. Build the application

**Windows:**

```powershell
.\mvnw.cmd clean package
```

**macOS / Linux:**

```bash
./mvnw clean package
```

The packaged artifact is generated in the `target/` directory.

## Database Migrations

Flyway migration scripts are located in:

```text
src/main/resources/db/migration/
```

The application runs the configured migrations during startup. Keep migration scripts in version control; they are part of the application source.

## Configuration Reference

| Property / Environment variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL for PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password |
| `SERVER_PORT` | HTTP port for the application |
| `APP_BASE_URL` | Base URL used when constructing short links |

The current local configuration uses `http://localhost:8081` as the base URL. If you change the server port, update the base URL accordingly.

## Project Structure

```text
Url-Shortner/
├── src/
│   ├── main/
│   │   ├── java/com/example/urlshortner/
│   │   │   ├── config/                 # Security and application configuration
│   │   │   └── domain/
│   │   │       ├── entities/           # JPA entities
│   │   │       ├── repositories/       # Data access
│   │   │       ├── services/           # Business logic
│   │   │       └── web/controllers/    # Web controllers
│   │   └── resources/
│   │       ├── db/migration/           # Flyway migrations
│   │       ├── static/                 # CSS and static assets
│   │       ├── templates/              # Thymeleaf pages
│   │       └── application.properties  # Application configuration
│   └── test/                           # Tests
├── .mvn/wrapper/                       # Maven Wrapper configuration
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Security Notes

- Never commit `.env` files, database backups, private keys, or production credentials.
- Use environment variables or a secrets manager for passwords.
- Change any sample or development credentials before deploying the application.
- Use HTTPS and review authentication, authorization, and URL validation before exposing the application publicly.

## Current Scope

This repository is a learning project. Configuration and deployment choices may need further hardening before production use.

## License

No license is specified yet. Add a `LICENSE` file if you intend to publish the project with explicit reuse permissions.
