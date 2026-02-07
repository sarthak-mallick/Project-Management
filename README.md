# Project Management Application

## 🚀 Features

- **User Authentication**: Secure login and registration system.
- **Project Management**: Create, update, and manage projects.
- **Task Management**: Create and assign tasks within projects.
- **Role-Based Access**: Manage user roles and permissions (implied).
- **Responsive UI**: JSP-based frontend with JSTL for dynamic content.

## 🛠 Tech Stack

- **Backend**: Java 21, Spring Boot 3.4.0 (MVC Architecture)
- **ORM**: Hibernate
- **Database**: MySQL 8.0.33
- **Frontend**: JSP, JSTL
- **Build Tool**: Maven
- **Server**: Apache Tomcat (Embedded & External Support)

## 📋 Prerequisites

Ensure you have the following installed:

- **Java JDK 21** or higher
- **Maven 3.6** or higher
- **MySQL Server**

## ⚙️ Configuration

### Database Setup

The application uses **MySQL**. By default, it expects a database named `projectmanagement` on `localhost:3306`.

1.  **Update Configuration**:
    Open `src/main/resources/hibernate.cfg.xml` and update the connection details if your MySQL setup differs:

    ```xml
    <property name="connection.url">jdbc:mysql://localhost:3306/projectmanagement?createDatabaseIfNotExist=true</property>
    <property name="connection.username">root</property>
    <property name="connection.password">password</property>
    ```

2.  **Schema Creation**:
    The application is configured with `hbm2ddl.auto` set to `update`. The database schema will be automatically created/updated when the application starts.

## 🏃‍♂️ Running the Application

### Run with Maven (Recommended for Development)

1.  Open a terminal in the project root.
2.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
3.  Access the application at: [http://localhost:8080](http://localhost:8080)

## 📂 Project Structure

```
com.projectmanagement
├── config          # Application configuration (WebConfig, etc.)
├── controllers     # Spring MVC Controllers
├── dao             # Data Access Objects
├── models          # Entity Models (User, Project, Task)
├── interceptor     # Session management and auth interceptors
├── validator       # Form validation logic
└── ProjectManagementApplication.java # Main entry point
```
