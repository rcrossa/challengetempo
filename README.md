# Tempo Project


## License and Disclaimer

This project is licensed under the MIT License. See the `LICENSE` file for details.

### Disclaimer

This software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, and noninfringement. In no event shall the authors or copyright holders be liable for any claim, damages, or other liability, whether in an action of contract, tort, or otherwise, arising from, out of, or in connection with the software or the use or other dealings in the software.

### Authorization

Use of this software is permitted only with explicit authorization from the authors. Unauthorized use is strictly prohibited.
## Description
This project is a Spring Boot application that manages transactions. Below are the instructions to start the project and use Docker to run the database.

## Requirements
- Java 17 or higher
- Maven 3.6.0 or higher
- Docker

## Configuration
1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/your-repository.git
    cd your-repository
    ```

2. Configure the environment variables in the `.env` file:
    ```dotenv
    DB_USER='postgres'
    DB_PASSWORD='<your_password>'
    DB_DATABASE='tempo'
    SONAR_TOKEN='your_sonar_token_here'
    ```

## Build and Run the Project

1. Build the project with Maven:
    ```bash
    mvn clean install
    ```

2. Run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```

The application will be available at `http://localhost:8080`.

## Docker 
### ATTENTION: Note on Docker Images

The provided `Dockerfile` uses an image for ARM architecture (`arm64v8/openjdk:17-jdk-slim`). If you are using a different architecture, such as x86_64, you will need to modify the `Dockerfile` to use a compatible image. For example, you can replace the base image with `openjdk:17-jdk-slim` for x86_64 architecture.

To determine your processor architecture, you can use the following command:

### Using Docker

There are two docker-compose files provided:  
docker-compose.yml: This file sets up both the database and the API.
docker-compose-db.yml: This file sets up only the database.

To run both the database and the API, use the following command:

```bash
docker-compose -f docker-compose-db.yml up --build
```

```bash
uname -m
```
1. Build the Docker image for the database:
    ```bash
    docker build -t tempo-db .
    ```

2. Run the Docker container for the database:
    ```bash
    docker run -d -p 5432:5432 --env-file .env tempo-db
    ```
  
 3. Run the Docker container for the api:
    ```bash
    docker build -t tempo-api .
    docker run -d -p 8080:8080 --env-file .env tempo-api
    ```

## Database Setup

Once the Docker container is running, follow these steps to set up the database and table:

1. Access the running Docker container:
    ```bash
    docker exec -it <container_id> /bin/bash
    ```

2. Connect to the PostgreSQL database:
    ```bash
    psql -U postgres -d tempo
    ```

3. Create the `transactions` table:
    ```sql
    CREATE TABLE transactions (
        id SERIAL PRIMARY KEY,
        amount DECIMAL(10, 2) NOT NULL,
        description VARCHAR(255),
        transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    ```

4. Verify the table creation:
    ```sql
    \dt
    ```

Replace `<container_id>` with the actual ID of your running Docker container. This will set up the necessary database and table for your application.

## Endpoints

- `GET /api/transactions` - Get all transactions
- `GET /api/transactions/{id}` - Get a transaction by ID
- `POST /api/transactions` - Create a new transaction
- `PUT /api/transactions/{id}` - Update an existing transaction
- `DELETE /api/transactions/{id}` - Delete a transaction

## Testing

By default, tests use an in-memory H2 database.  
No PostgreSQL instance or environment variables are required for running tests.

To run tests:
```bash
mvn clean test
```

If you want to run tests against PostgreSQL, remove or adjust the `@ActiveProfiles("test")` annotation in `ChallengeApplicationTests.java`.

## SonarQube Analysis

To run SonarQube analysis, make sure you have:
1. SonarQube server running on `http://localhost:9000`
2. `SONAR_TOKEN` configured in your `.env` file or as environment variable

Run the analysis with:
```bash
mvn clean test sonar:sonar -Dsonar.token=${SONAR_TOKEN}
```

Or set the token directly:
```bash
mvn clean test sonar:sonar -Dsonar.token=your_token_here
```

The SonarQube configuration is defined in `sonar-project.properties` file.
