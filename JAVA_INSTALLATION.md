# Java Installation Guide for Windows

## Method 1: Download and Install Java 17 (Recommended)

### Step 1: Download Java
1. Go to https://adoptium.net/temurin/releases/
2. Select **Java 17 (LTS)**
3. Choose **Windows x64** architecture
4. Download the **JDK** (not JRE)

### Step 2: Install Java
1. Run the downloaded installer
2. Follow the installation wizard
3. **Important**: Note the installation path (usually `C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot\`)

### Step 3: Set Environment Variables
1. Open **System Properties** → **Advanced** → **Environment Variables**
2. Add new **System Variable**:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot` (your actual path)
3. Edit **PATH** variable and add: `%JAVA_HOME%\bin`

### Step 4: Verify Installation
1. Open **Command Prompt**
2. Run: `java -version`
3. Run: `javac -version`
4. Both should show Java 17

## Method 2: Using Chocolatey (if you have it)
```bash
choco install openjdk17
```

## Method 3: Using Scoop (if you have it)
```bash
scoop install openjdk17
```

## Method 4: Using SDKMAN (if you have it)
```bash
sdk install java 17.0.9-tem
```

## Troubleshooting

### If JAVA_HOME is not recognized:
1. Restart Command Prompt after setting environment variables
2. Check if the path in JAVA_HOME is correct
3. Ensure there are no spaces in the path
4. Try using quotes around the path if it contains spaces

### If java command is not found:
1. Check if `%JAVA_HOME%\bin` is in your PATH
2. Restart your computer after setting environment variables
3. Try opening a new Command Prompt window

## Quick Test
After installation, run these commands:
```bash
java -version
javac -version
echo %JAVA_HOME%
```

All three should return valid responses.
