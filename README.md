# Smart Study Planner


An Integrated Time Management Solution for Academic Achievement


## Project Overview


Smart Study Planner is a comprehensive application designed to help students effectively manage their academic workload across diverse subjects with different priority levels and concurrent deadlines. The system enables students to organize subject material hierarchically, schedule study sessions with intelligent time allocation, track progress through data visualization, and optimize study efficiency through an intuitive and responsive user interface.


## Problem Statement


Contemporary students encounter multifaceted challenges in effectively managing their academic workload across diverse subjects with heterogeneous priority levels and concurrent deadlines. The deficit of a purpose-built, interactive platform specifically engineered for academic time management leads to suboptimal resource allocation, missed submission deadlines, and elevated cognitive stress levels. The Smart Study Planner addresses these challenges by:


- Providing hierarchical organization of academic content
- Implementing intelligent time allocation
- Enabling progress tracking with data visualization
- Offering an intuitive and responsive user interface
- Reducing cognitive overhead associated with manual planning


## Key Features


### User Management
- Secure account creation with email verification
- JWT-based authentication
- Role-based access control (Student and Admin roles)
- User profile management
- Password reset functionality


### Subject Management
- Complete lifecycle management for academic subjects
- Three-tiered priority assignment (High, Medium, Low)
- Subject enrollment/unenrollment
- Course materials attachment (documents, links, resources)
- Subject analytics (completion rates, time allocation)


### Task Management
- Comprehensive task lifecycle management
- Task association with subjects
- Priority assignment aligned with subject priorities
- Task completion tracking with timestamps
- Recurring tasks with customizable frequency
- Task dependencies and prerequisite relationships
- Email notifications for upcoming and overdue tasks


### Progress Tracking
- Study session logging with time spent tracking
- Date-based analytics for progress entries
- Visual progress representation through charts and graphs
- Streak system for tracking consecutive days of study
- Performance trends analysis


### Data Management
- CSV import/export functionality
- Template generation for data import
- Thorough validation of imported data


### Analytics
- Study time analytics by subject
- Task completion analysis
- Productivity scoring based on consistency and completion
- Performance trends visualization
- Subject comparison analytics


## Technology Stack


### Frontend
- Angular v16+: Component-based frontend framework implementing MVVM architecture
- Angular Material: Comprehensive design component library with theming support
- Chart.js: JavaScript library for creating interactive charts and data visualizations
- Bootstrap 5: CSS framework for responsive and mobile-first design


### Backend
- Java 17: Core programming language with modern language features
- Spring Boot 3.1.x: Application framework with dependency injection, web MVC, and security modules
- Spring Data JPA: Data access abstraction for simplified database operations
- Spring Security: Authentication and authorization framework
- JWT (JSON Web Tokens): Stateless authentication mechanism
- Apache Commons CSV: Library for reading and writing CSV files


### Database
- PostgreSQL 14+: Advanced open-source relational database with JSON support, full-text search capabilities, and transactional integrity


### Development Tools
- Maven: Dependency management tool with standardized project structure
- Git/GitHub: Distributed version control system
- IntelliJ IDEA: Integrated development environment
- Postman: API testing and documentation platform


## Object-Oriented Concepts Implemented


### 1. Inheritance
- BaseEntity abstract class providing fundamental entity attributes
- User → Admin class hierarchy
- Task → RecurringTask class hierarchy


### 2. Encapsulation
- Information hiding through private data members
- Data validation in setter methods
- Business logic encapsulation within service classes


### 3. Polymorphism
- Interface implementation (Exportable interface)
- Method overriding in subclasses
- Strategy pattern for scheduling algorithms
- Dynamic method dispatch


### 4. Abstraction
- Abstract base classes for common functionality
- Interface contracts for data access
- Method abstraction behind simple signatures
- Separation of concerns between layers


### 5. Design Patterns
- **Builder Pattern:** For entity creation
- **Factory Pattern:** For creating specialized objects
- **Strategy Pattern:** For scheduling algorithms
- **Repository Pattern:** For data access abstraction
- **Template Method Pattern:** For email services


## Project Structure


```
smart-study-planner/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/studyplanner/
│   │   │   │   ├── config/         # Configuration classes
│   │   │   │   ├── controller/     # REST API controllers
│   │   │   │   ├── dto/            # Data Transfer Objects
│   │   │   │   ├── entity/         # JPA entity classes
│   │   │   │   ├── exception/      # Custom exceptions
│   │   │   │   ├── repository/     # Spring Data JPA repositories
│   │   │   │   ├── security/       # Security configurations
│   │   │   │   ├── service/        # Business logic services
│   │   │   │   ├── strategy/       # Strategy pattern implementations
│   │   │   │   └── util/           # Utility classes
│   │   │   └── resources/          # Application properties, etc.
│   │   └── test/                   # Unit and integration tests
│   └── pom.xml                     # Maven configuration
│
└── frontend/
   ├── src/
   │   ├── app/
   │   │   ├── auth/               # Authentication components
   │   │   ├── dashboard/          # Dashboard components
   │   │   ├── subjects/           # Subject management
   │   │   ├── tasks/              # Task management
   │   │   ├── progress/           # Progress tracking
   │   │   ├── analytics/          # Analytics visualization
   │   │   ├── shared/             # Shared components
   │   │   ├── core/               # Core services
   │   │   └── models/             # TypeScript interfaces
   │   ├── assets/                 # Static assets
   │   └── environments/           # Environment configurations
   ├── angular.json                # Angular CLI configuration
   └── package.json                # NPM dependencies
```


## Setup Instructions


### Prerequisites
- Java 17 or higher
- Node.js 14.x or higher
- npm 6.x or higher
- PostgreSQL 14+
- Maven 3.6+


### Backend Setup
1. Clone the repository:
  ```bash
  git clone https://github.com/CSYE6200-Object-Oriented-Design-Spr25/csye6200-spring-2025-final-project-final_project_group_4.git
  cd csye6200-spring-2025-final-project-final_project_group_4/backend
  ```


2. Configure the database in `src/main/resources/application.properties`:
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/studyplanner
  spring.datasource.username=yourUsername
  spring.datasource.password=yourPassword
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
  ```


3. Build and run the backend:
  ```bash
  mvn clean install
  mvn spring-boot:run
  ```
  The backend will start on http://localhost:8080


### Frontend Setup
1. Navigate to the frontend directory:
  ```bash
  cd ../frontend
  ```


2. Install dependencies:
  ```bash
  npm install
  ```


3. Run the development server:
  ```bash
  ng serve
  ```
  The application will be available at http://localhost:4200


## API Documentation


The backend exposes a RESTful API with the following main endpoints:


### Authentication
- `POST /api/auth/register`: Register a new user
- `POST /api/auth/login`: Authenticate and get JWT token
- `POST /api/auth/refresh-token`: Refresh the JWT token
- `POST /api/auth/forgot-password`: Initiate password reset


### Subjects
- `GET /api/subjects`: Get all subjects for current user
- `GET /api/subjects/{id}`: Get specific subject
- `POST /api/subjects`: Create a new subject
- `PUT /api/subjects/{id}`: Update a subject
- `DELETE /api/subjects/{id}`: Delete a subject
- `POST /api/subjects/enroll/{id}`: Enroll in a subject
- `POST /api/subjects/unenroll/{id}`: Unenroll from a subject


### Tasks
- `GET /api/tasks`: Get all tasks for current user
- `GET /api/tasks/{id}`: Get specific task
- `POST /api/tasks`: Create a new task
- `PUT /api/tasks/{id}`: Update a task
- `DELETE /api/tasks/{id}`: Delete a task
- `PUT /api/tasks/{id}/complete`: Mark task as complete
- `GET /api/tasks/subject/{subjectId}`: Get tasks for a subject
- `GET /api/tasks/overdue`: Get overdue tasks


### Progress
- `GET /api/progress`: Get all progress entries
- `GET /api/progress/task/{taskId}`: Get progress entries for a task
- `POST /api/progress`: Log a new progress entry
- `GET /api/progress/analytics`: Get progress analytics


### CSV Operations
- `GET /api/export/subjects`: Export subjects to CSV
- `GET /api/export/tasks`: Export tasks to CSV
- `POST /api/import/subjects`: Import subjects from CSV
- `POST /api/import/tasks`: Import tasks from CSV


## Contributors


- **Diksha Sahare** - Backend development (User authentication, JWT token implementation), Frontend development (Dashboard components, Progress tracking features), and Database design (User and Progress tables)
- **Mittul Sharma** - Backend development (Subject and Task management, CSV file handling), Frontend development (User interface, Angular components), and Database design (Subject and Task tables)


## License


This project is licensed under the MIT License - see the LICENSE file for details.


## Acknowledgments


- Northeastern University
- CSYE6200 - Object-Oriented Design
- Spring 2025


---


© 2025 Smart Study Planner | CSYE6200 - Object-Oriented Design

