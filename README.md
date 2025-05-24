# Admin Panel Backend

This is a Spring Boot backend application that provides authentication, user management, and other functionalities for the Admin Panel.

## Features

- User Authentication (Login/Register)
- Email Verification
- Country-based Registration Restriction
- JWT Token-based Authentication
- PostgreSQL Database Integration
- Email Notifications

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- GeoLite2 Country Database

## Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd admin-panel
```

2. Configure PostgreSQL:
- Create a new database named `admin_panel`
- Update `src/main/resources/application.properties` with your database credentials

3. Configure Email Settings:
- Update `src/main/resources/application.properties` with your email credentials
- For Gmail, you'll need to generate an App Password

4. Configure GeoIP:
- Download the GeoLite2 Country database from MaxMind
- Place the `GeoLite2-Country.mmdb` file in `src/main/resources/`

5. Configure JWT:
- Update the JWT secret key in `application.properties`

## Build and Run

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login user
- POST `/api/auth/verify-email` - Verify email
- POST `/api/auth/resend-verification` - Resend verification code

### Protected Endpoints
All other endpoints require JWT authentication via Bearer token

## Security

- Passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- Email verification is required
- Registration is restricted for certain countries
- Input validation and sanitization

## Development

To run the application in development mode with hot reload:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Testing

Run tests with:
```bash
mvn test
``` 