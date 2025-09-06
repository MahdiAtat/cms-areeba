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

### Additional Features

- Add endpoint to list all cardIds with pagination
- Add endpoint to list all cardIds for specific account with pagination

### Backend

- **RESTful API**: Java SpringBoot
- **Database**: PostgresSql with Hibernate ORM
- **API Documentation**: Swagger/OpenAPI documentation
- **Logging**: Lombok for structured logging
- **Testing**: Junit 5 and Mockito
- **Monitoring**: Springboot health checks

This project is built with the help of `openapi-generator-maven-plugin` that generates from the swagger the respective interfaces and classes


## 🏗️ Architecture

```
cms
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
│   └───swagger
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
    └───swagger

```

## 📚 Documentation

- **API Documentation**: Interactive Swagger documentation available at `/cms/v1/swagger-ui.html` or `/fraud/v1/swagger-ui.html` when the backend is running

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
```

### 2. Environment Configuration

#### Environment Files
#### `env.example`

- Template file with all required environment variables
- Safe to commit to version control
- Contains placeholder values and documentation

Edit `env.local` with your configuration

### 3. Start Development Servers

```bash
 docker compose --env-file env.local up -d --build
```

This will start:

- **CMS MircoService**: http://localhost:8080/cms/v1
- **CMS API Documentation**: http://localhost:8080/cms/v1/swagger-ui.html

- **FRAUD MircoService**: http://localhost:8090/fraud/v1
- **FRAUD API Documentation**: http://localhost:8090/fraud/v1/swagger-ui.html

## 🧪 Testing

### Run All Unit Tests

```bash
mvn clean test 
```

### Run Jmeter tests
- Jmeter file with all endpoints is already set in the cms folder. All you need to do is open jmeter with `cms.jmx` file and update the endpoint variables based on the ids generated. This will run all the endpoints for all microservices.

## 🗄️ Database Schema

**cms_cmsMicroService**: The application uses a relational database with the following main entities:

- **Accounts**: Core accounts information
- **Cards**: Cards related to accounts
- **Transactions**: Transactions done by each account

**cms_fraudMicroService**: The application uses a relational database with the following main entities:

- **fraud_events**: Fraud events related to transactions in case accepted or rejected

## 👨‍💻 Author

**MahdiAtat**