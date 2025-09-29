# MDD - Monde de Dev - Full Stack Application

## Architecture

This full-stack application follows a modern architecture with strict frontend/backend separation:

- **Backend**: Java 21 LTS + Spring Boot 3.5 with Spring Security and JWT
- **Frontend**: Angular 20 with Signals, Standalone Components and Angular Material
- **Database**: PostgreSQL 16 with dedicated SQL scripts
- **Containerization**: Docker with docker-compose
- **Documentation**: OpenAPI/Swagger integration
- **Authentication**: JWT with refresh tokens and blacklist mechanism

## Quick Start with Docker

```bash
# Clone the repository
git clone <repository-url>
cd Developpez-une-application-full-stack-complete

# Setup environment variables
cp .env.example .env
# Edit .env with your secure values

# Launch with Docker Compose
docker-compose up --build

# Access the application
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080
# API Documentation: http://localhost:8080/swagger-ui.html
# Database: localhost:5432
```

## Prerequisites

- **Docker & Docker Compose** (recommended)
- **Node.js 18+** and npm
- **Java 21** and Maven
- **PostgreSQL 16+**

## Local Development

### Backend (Spring Boot)

```bash
cd back

# Install dependencies and compile
./mvnw clean install

# Launch in development mode
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### Frontend (Angular)

```bash
cd front

# Install dependencies
npm install

# Launch development server
npm start

# Run tests
npm test

# Production build
npm run build
```

## Configuration

### Environment Variables Setup

1. **Copy the environment template:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your secure values:**
   ```properties
   # Database Configuration
   POSTGRES_DB=mdd_db
   POSTGRES_USER=mdd_user
   POSTGRES_PASSWORD=your_secure_password_here

   # Backend Database Connection
   SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mdd_db
   SPRING_DATASOURCE_USERNAME=mdd_user
   SPRING_DATASOURCE_PASSWORD=your_secure_password_here

   # JWT Configuration
   JWT_SECRET=your_jwt_secret_key_at_least_32_characters_long_for_HS256
   JWT_EXPIRATION=86400000
   JWT_REFRESH_EXPIRATION=604800000

   # Spring Profile
   SPRING_PROFILES_ACTIVE=docker

   # JPA Configuration
   SPRING_JPA_HIBERNATE_DDL_AUTO=update
   ```

3. **Important Security Notes:**
   - Never commit the `.env` file to version control
   - Use strong, unique values for `JWT_SECRET` and `POSTGRES_PASSWORD`
   - The `.env` file is already added to `.gitignore`

### Frontend Configuration

Environment configurations are located in `front/src/environments/`

## Features

### Implemented Features

- **Authentication System**
  - User registration with email validation
  - Login with JWT tokens
  - Token refresh mechanism
  - Password validation with custom directives
  - Token blacklist for secure logout

- **Theme Management**
  - Create, read, update, delete themes
  - Subscribe/unsubscribe to themes
  - Theme-based article filtering

- **Article Management**
  - Create articles linked to themes
  - View articles with detailed information
  - Article listing with pagination
  - Author information display

- **Comment System**
  - Add comments to articles
  - Comment threading and display
  - User-specific comment management

- **User Profile**
  - Profile viewing and editing
  - Subscription management
  - User information updates

### Frontend Architecture (Angular 20)

- **Standalone Components**: Modern Angular architecture without NgModules
- **Signals**: Angular's new reactivity API for state management
- **Angular Material**: Consistent UI components and theming
- **Functional Routing**: Simplified route configuration with guards
- **Reactive Forms**: Form validation and user input handling
- **HTTP Interceptors**: Automatic token injection and error handling

### Backend Architecture (Spring Boot 3.5)

- **REST API**: RESTful endpoints with proper HTTP status codes
- **Spring Security**: JWT-based authentication and authorization
- **JPA/Hibernate**: Database entities and repositories
- **Exception Handling**: Global exception handling with custom exceptions
- **Validation**: Request validation using Bean Validation annotations
- **OpenAPI Documentation**: Auto-generated API documentation

## Security

- **JWT Authentication**: Secure token-based authentication
- **Token Blacklist**: Invalidated tokens tracking
- **Password Validation**: Custom password strength requirements
- **CORS Configuration**: Proper cross-origin resource sharing setup
- **Spring Security**: Endpoint protection and user authorization
- **Input Validation**: Both frontend and backend validation

## Database Schema

The PostgreSQL schema includes:
- **users**: Application users with authentication data
- **themes**: Discussion themes/topics
- **articles**: User-generated articles linked to themes
- **comments**: Comments on articles with user references
- **subscriptions**: User subscriptions to themes

## API Documentation

Access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## Testing

### Backend Testing
```bash
./mvnw clean test                    # Run all backend tests
./mvnw test -Dtest=ClassName         # Run specific test class
```

### Frontend Testing
```bash
npm test                             # Run unit tests with Karma
npm run test:watch                   # Run tests in watch mode
```

## Useful Scripts

```bash
# Backend
./mvnw clean test                    # Backend tests
./mvnw spring-boot:run              # Launch API
./mvnw clean compile                 # Compile project

# Frontend
npm test                            # Frontend tests
npm run build                       # Production build
npm start                           # Development server

# Docker
docker-compose up --build           # Rebuild and launch all services
docker-compose down                 # Stop all services
docker-compose logs backend         # View backend logs
docker-compose logs frontend        # View frontend logs
```

## Project Structure

```
.
├── back/                           # Spring Boot backend
│   ├── src/main/java/com/openclassrooms/mddapi/
│   │   ├── controller/            # REST controllers
│   │   ├── service/               # Business logic services
│   │   ├── repository/            # Data access repositories
│   │   ├── model/                 # JPA entities
│   │   ├── dto/                   # Data transfer objects
│   │   ├── security/              # Security configuration
│   │   ├── config/                # Application configuration
│   │   ├── exception/             # Custom exceptions
│   │   └── validation/            # Custom validators
│   └── Dockerfile
├── front/                          # Angular frontend
│   ├── src/app/
│   │   ├── components/            # Reusable UI components
│   │   ├── pages/                 # Page components
│   │   ├── services/              # HTTP services
│   │   ├── models/                # TypeScript interfaces
│   │   ├── guards/                # Route guards
│   │   ├── interceptors/          # HTTP interceptors
│   │   └── validators/            # Form validators
│   └── Dockerfile
├── scripts/sql/                    # Database initialization scripts
└── docker-compose.yml             # Multi-container Docker setup
```

## Resources

- [Angular Material](https://material.angular.io/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT.io](https://jwt.io/) - JWT token debugging