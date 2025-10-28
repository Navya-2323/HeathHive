# Maven Installation Guide for Windows

## Method 1: Download and Install Maven

1. **Download Maven:**
   - Go to https://maven.apache.org/download.cgi
   - Download the latest Binary zip archive (e.g., apache-maven-3.9.6-bin.zip)

2. **Extract Maven:**
   - Extract the zip file to a folder like `C:\Program Files\Apache\maven`
   - Or any location you prefer (avoid spaces in path)

3. **Set Environment Variables:**
   - Open System Properties → Advanced → Environment Variables
   - Add new system variable:
     - Variable name: `MAVEN_HOME`
     - Variable value: `C:\Program Files\Apache\maven` (your Maven path)
   - Edit PATH variable and add: `%MAVEN_HOME%\bin`

4. **Verify Installation:**
   - Open Command Prompt
   - Run: `mvn --version`

## Method 2: Using Chocolatey (if you have it)
```bash
choco install maven
```

## Method 3: Using Scoop (if you have it)
```bash
scoop install maven
```

## Method 4: Using SDKMAN (if you have it)
```bash
sdk install maven
```
