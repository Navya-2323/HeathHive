# Intelligent Health Assistant

A mini intelligent health assistant built with Java Spring Boot that processes health data, runs it through a pre-trained ML model, calculates risk percentages, and generates PDF reports.

## Features

- **Health Data Processing**: Accepts comprehensive health data including vitals, medical history, and lifestyle factors
- **ML Risk Assessment**: Uses a weighted algorithm to calculate health risk percentages (Low/Medium/High)
- **PDF Report Generation**: Creates detailed PDF reports with risk assessment and recommendations
- **Multithreaded Processing**: Handles multiple user requests simultaneously using async processing
- **REST API**: Clean REST endpoints for health data submission and retrieval
- **Database Integration**: Uses H2 in-memory database with JPA/Hibernate

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database**
- **iText7** (PDF generation)
- **Apache Commons Math3** (ML calculations)
- **Maven** (Dependency management)

## Project Structure

```
src/
├── main/
│   ├── java/com/healthassistant/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST API controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Data access layer
│   │   ├── service/        # Business logic services
│   │   └── IntelligentHealthAssistantApplication.java
│   └── resources/
│       └── application.yml # Application configuration
└── test/
    └── java/com/healthassistant/
        └── controller/      # Test classes
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd intelligent-health-assistant
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Health Assessment
- **POST** `/api/health/assess` - Submit health data for assessment (async)
- **POST** `/api/health/assess/sync` - Submit health data for assessment (sync)

### Data Retrieval
- **GET** `/api/health/assessments/{email}` - Get all assessments for a user
- **GET** `/api/health/assessments/{assessmentId}` - Get specific assessment

### System
- **GET** `/api/health/health` - Health check endpoint
- **GET** `/api/health/stats` - System statistics


## Risk Assessment Algorithm

The ML model uses weighted factors to calculate risk:

- **Age**: 0.5 points per year
- **BMI**: 5-20 points based on category
- **Blood Pressure**: 8-15 points for abnormal readings
- **Heart Rate**: 5 points for abnormal rates
- **Cholesterol**: 12 points for high levels
- **Blood Sugar**: 8-15 points for elevated levels
- **Lifestyle**: -8 to +25 points based on habits
- **Medical History**: 12-25 points for existing conditions

Risk Levels:
- **Low**: 0-30%
- **Medium**: 30-70%
- **High**: 70-100%

## Database

The application uses H2 in-memory database for development. Access the console at:
`http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:healthdb`
- **Username**: `sa`
- **Password**: `password`

## Multithreading

The application uses a thread pool executor with:
- Core pool size: 5 threads
- Maximum pool size: 20 threads
- Queue capacity: 100 requests
- Thread name prefix: `health-async-`

## PDF Reports

Generated PDF reports include:
- Patient information
- Health metrics
- Risk assessment with color coding
- Risk factors
- Recommendations
- Medical disclaimer

Reports are saved in the `reports/` directory.

## Testing

Run tests with:
```bash
mvn test
```

## Configuration

Key configuration options in `application.yml`:

```yaml
health-assistant:
  pdf:
    reports-directory: reports
    max-file-size: 5MB
  ml:
    risk-threshold-low: 30.0
    risk-threshold-medium: 70.0
  async:
    thread-pool-size: 10
    timeout-seconds: 30
```

## Future Enhancements

- Integration with real ML models (TensorFlow, scikit-learn)
- User authentication and authorization
- Email notifications
- Advanced analytics dashboard
- Integration with wearable devices
- Real-time health monitoring

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Disclaimer

This application is for educational and demonstration purposes only. It should not be used as a substitute for professional medical advice, diagnosis, or treatment. Always consult with qualified healthcare providers for proper medical evaluation.

