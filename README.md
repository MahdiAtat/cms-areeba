# Card Management System

A modern, backend card management system built with java springboot, postgresql. This monorepo contains both the card management system microservice and the fraud microservice for managing accounts, cards and transactions.

## 🚀 Features

### CMS microservice

- **Accounts**: CRUD, balance tracking, ACTIVE/INACTIVE
- **Cards**: Create, activate/deactivate, masked PAN in responses, encrypted at rest
- **Transactions**: 
  - Debit/Credit with validations (card ownership/status/expiry, account status, sufficient funds) 
  - Persist approved and rejected results
- **Docs**: Swagger/OpenAPI documentation (cms/v1/swagger-ui.html)

### Fraud microservice

- **Logic**: 
  - Rejects when amount > $10,000 or ≥ 8 transactions in last hour
  - Called from Transactions via Feign
- **Docs**: Swagger/OpenAPI documentation (fraud/v1/swagger-ui.html)

### Backend

- **RESTful API**: Java SpringBoot
- **Database**: PostgresSql with Hibernate ORM
- **API Documentation**: Swagger/OpenAPI documentation
- **Logging**: Lombok for structured logging
- **Testing**: Junit 5 and Mockito

## 🏗️ Architecture

```
cms
|
├───cmsmicroservice
│   ├───src
│   │   ├───main
│   │   │   ├───java
│   │   │   │   └───com
│   │   │   │       └───areeba
│   │   │   │           └───cms
│   │   │   │               └───cmsmircoservice
│   │   │   │                   ├───accounts
│   │   │   │                   │   ├───controller
│   │   │   │                   │   ├───repo
│   │   │   │                   │   └───service
│   │   │   │                   │       └───impl
│   │   │   │                   ├───cards
│   │   │   │                   │   ├───controller
│   │   │   │                   │   ├───repo
│   │   │   │                   │   └───service
│   │   │   │                   │       └───impl
│   │   │   │                   ├───config
│   │   │   │                   ├───exception
│   │   │   │                   ├───handler
│   │   │   │                   ├───rest
│   │   │   │                   ├───transactions
│   │   │   │                   │   ├───controller
│   │   │   │                   │   ├───repo
│   │   │   │                   │   └───service
│   │   │   │                   │       └───impl
│   │   │   │                   ├───type
│   │   │   │                   └───utils
│   │   │   └───resources
│   │   │       └───db
│   │   │           └───postgresql
│   │   └───test
│   │       └───java
│   │           └───com
│   │               └───areeba
│   │                   └───cms
│   │                       └───cmsmircoservice
│   │                           ├───Accounts
│   │                           ├───Cards
│   │                           └───Transactions
│   ├───swagger
|
|
└───fraudmicroservice
    ├───src
    │   ├───main
    │   │   ├───java
    │   │   │   └───com
    │   │   │       └───areeba
    │   │   │           └───cms
    │   │   │               └───fraudmicroservice
    │   │   │                   ├───controller
    │   │   │                   ├───handler
    │   │   │                   ├───repo
    │   │   │                   ├───service
    │   │   │                   │   └───impl
    │   │   │                   └───type
    │   │   └───resources
    │   │       └───db
    │   │           └───postgresql
    │   └───test
    │       └───java
    │           └───com
    │               └───areeba
    │                   └───cms
    │                       └───fraudmicroservice
    │                           └───Fraud
    ├───swagger
    └───target
        ├───classes
            ├───com
            │   └───areeba
            │       └───cms
            │           └───fraudmicroservice
            │               ├───controller
            │               ├───handler
            │               ├───repo
            │               ├───service
            │               │   └───impl
            │               └───type
            └───db
               └───postgresql

```

## 📚 Documentation

- **API Documentation**: Interactive Swagger documentation available at `/cms/v1/swagger-ui.html` or `/fraud/v1/swagger-ui.html` when the backend is running
- **Environment Setup**: Copy `env.example` files in `.` directory.

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
```

### 2. Environment Configuration

Edit `cms/env.local` with your configuration:

```env
# Database
DATABASE_URL="mysql://username:password@localhost:3306/employee_directory"

# Server
PORT=5001
NODE_ENV=development
HOST=localhost

# JWT
JWT_ACCESS_SECRET=your-super-secret-access-token-key-here
JWT_ACCESS_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=3600

# AWS
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-employee-directory-bucket
AWS_CLOUDFRONT_DOMAIN=your-cloudfront-domain.cloudfront.net

# CORS
CORS_ORIGIN=http://localhost:5173

# Security
BCRYPT_ROUNDS=12
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100
```

### 3. Start Development Servers

```bash
# Start in development mode
 docker-compose --env-file env.local up
```

This will start:

- **CMS MircoService**: http://localhost:8080/cms/v1
- **CMS API Documentation**: http://localhost:8080/cms/v1/swagger-ui.html

- **FRAUD MircoService**: http://localhost:8090/fraud/v1
- **FRAUD API Documentation**: http://localhost:8090/fraud/v1/swagger-ui.html

## 🧪 Testing

### Run All Tests

```bash
mvn clean test 
```

## 🗄️ Database Schema

**cms_cmsMicroService**: The application uses a relational database with the following main entities:

- **Accounts**: Core accounts information
- **Cards**: Cards related to accounts
- **Transactions**: Transactions done by each account

**cms_fraudMicroService**: The application uses a relational database with the following main entities:

- **fraud_events**: Fraud events related to transactions in case accepted or rejected

## 👨‍💻 Author

**MahdiAtat**