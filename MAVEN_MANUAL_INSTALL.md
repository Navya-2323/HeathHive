# Manual Maven Installation Guide

## Step 1: Download Maven
1. Go to: https://maven.apache.org/download.cgi
2. Download: **apache-maven-3.9.6-bin.zip** (Binary zip archive)
3. Save it to your Downloads folder

## Step 2: Extract Maven
1. Extract the zip file to: `C:\Program Files\Apache\`
2. You should have: `C:\Program Files\Apache\apache-maven-3.9.6\`

## Step 3: Set Environment Variables
1. Open **System Properties** → **Advanced** → **Environment Variables**
2. Add new **System Variable**:
   - Variable name: `MAVEN_HOME`
   - Variable value: `C:\Program Files\Apache\apache-maven-3.9.6`
3. Edit **PATH** variable and add: `%MAVEN_HOME%\bin`

## Step 4: Verify Installation
1. **Restart Command Prompt**
2. Run: `mvn --version`
3. You should see Maven version information

## Step 5: Run Your Project
1. Navigate to your project directory:
   ```bash
   cd "C:\Users\chotu\OneDrive\Desktop\health"
   ```
2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

## Alternative: Use IDE
If Maven installation is difficult, use an IDE:
- **IntelliJ IDEA**: Open project → Run `IntelligentHealthAssistantApplication`
- **Eclipse**: Import Maven project → Run as Java Application
- **VS Code**: Install Java Extension Pack → Run main class

## Quick Test
After installation, test with:
```bash
mvn --version
java -version
echo %JAVA_HOME%
```

All three should return valid responses.
