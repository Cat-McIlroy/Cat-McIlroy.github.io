Medicines Shortages Assistant

This project provides an application to track and manage medicines shortages.

Requirements

Before running the application, make sure you have the following installed on your machine:

1. Java 21 - Required for running the Spring Boot backend.
2. PostgreSQL - This project uses PostgreSQL as the database.
3. Docker - Required to run the frontend in a container.
4. Maven - For building the backend application.

Setup Instructions

1. Set Up PostgreSQL Database

Ensure that PostgreSQL is installed and running on your machine.

- Create Database:

Once PostgreSQL is installed, start the PostgreSQL service and create the `postgres` database:

psql -U postgres
CREATE DATABASE postgres;
ALTER USER postgres WITH PASSWORD 'password';

2. Run the Backend

To run the backend, you will use the Spring Boot Maven plugin:

1. Open a terminal and navigate to the 'medicines-shortages-assistant' project directory.
2. Run the backend with Maven:

./mvnw clean spring-boot:run

This will start the backend on http://localhost:8080.

3. Run the Frontend with Docker

The frontend is served through Docker using Nginx. The Docker setup will serve static files.

1. Build and start the frontend container using Docker Compose:

docker-compose up --build

This will:

- Start the Nginx server and map port 5500 of your host machine to port 80 of the container.
- Serve the static frontend files from `src/main/resources/static` at http://localhost:5500.

4. Access the Application

- Frontend: The frontend will be accessible at http://localhost:5500.

Notes:

- PostgreSQL Connection: The backend will connect to the local PostgreSQL instance using the following credentials:
  - Username: postgres
  - Password: password

- The Frontend source code can be found at /medicines-shortages-assistant/src/main/resources/static
