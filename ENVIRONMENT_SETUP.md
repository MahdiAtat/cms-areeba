# Environment Setup Guide

This project uses environment files to manage configuration and secrets securely.

## Quick Start

1. **Copy the environment template:**

   ```bash
   cp env.example env.local
   ```

2. **Update the values in `env.local`** with your actual configuration:

   - Change `POSTGRES_PASSWORD` to a secure password
   - Update any other values as needed

3. **Run the application:**
   ```bash
   docker-compose --env-file env.local up
   ```

## Environment Files

### `env.example`

- Template file with all required environment variables
- Safe to commit to version control
- Contains placeholder values and documentation

### `env.local`

- Your local development configuration
- Contains actual secrets and configuration
- **Never commit this file to version control**

## Environment Variables

### Database Configuration

- `POSTGRES_DB`: Database name
- `POSTGRES_USER`: Database username
- `POSTGRES_PASSWORD`: Database password (change this!)
- `POSTGRES_PORT`: External port for database access

### Spring Configuration

- `SPRING_PROFILES_ACTIVE`: Active Spring profile
- `CMS_DATASOURCE_URL`: CMS service database URL
- `FRAUD_DATASOURCE_URL`: Fraud service database URL
- `DATASOURCE_USERNAME`: Database username for services
- `DATASOURCE_PASSWORD`: Database password for services

### Service Configuration

- `CMS_SERVICE_PORT`: CMS microservice port
- `FRAUD_SERVICE_PORT`: Fraud microservice port
- `POSTGRES_CONTAINER_NAME`: PostgreSQL container name
- `CMS_CONTAINER_NAME`: CMS microservice container name
- `FRAUD_CONTAINER_NAME`: Fraud microservice container name

## Security Notes

- All environment files except `env.example` are ignored by git
- Never commit actual secrets to version control
- Use strong passwords in production environments
- Consider using Docker secrets or external secret management for production

## Docker Compose Usage

The `docker-compose.yml` file uses environment variable substitution with default values:

```yaml
environment:
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-admin}
```

This means:

- Use the value from `POSTGRES_PASSWORD` environment variable
- If not set, use the default value `admin`

## Production Deployment

For production, create environment-specific files:

- `env.prod` for production
- `env.staging` for staging
- etc.

Then run with the appropriate environment file:

```bash
docker-compose --env-file env.prod up
```
