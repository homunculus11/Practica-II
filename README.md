# Practica Anul II - Complete Windows 10 Setup Guide

## 🎯 Project Overview

**Variant 16: Cursuri Online (Courses, Professors, Lessons)**

A JavaFX GUI application for managing online courses with an associated SQL database schema. This guide provides complete step-by-step instructions for setting up the project on Windows 10.

---

## 📋 System Requirements

### Minimum Requirements
- **Operating System**: Windows 10 (64-bit)
- **RAM**: 4 GB minimum, 8 GB recommended
- **Disk Space**: 2 GB free space
- **Administrator Privileges**: Required for JDK installation

### Software Requirements
- **JDK 25** (Java Development Kit)
- **JavaFX 25 SDK**
- **Windows PowerShell** (pre-installed on Windows 10)

---

## 🚀 Step-by-Step Setup Guide

### Step 1: Download and Extract the Project

1. **Download the project zip file** from your course platform or repository
2. **Extract the zip file** to your desired location:
   - Right-click the zip file
   - Select "Extract All..."
   - Choose a location like `C:\Users\YourName\Desktop\` or `C:\Projects\`
   - After extraction, you should have a folder named `Practica-II`

3. **Navigate to the project folder**:
   ```
   C:\Users\YourName\Desktop\Practica-II
   ```

---

### Step 2: Install JDK 25

**You have two options for JDK installation:**

#### Option A: Automatic Installation (Recommended)
The project includes a bundled JDK installer for easy setup.

1. **Check if JDK is already installed**:
   - Open Command Prompt or PowerShell
   - Type: `java -version`
   - If you see version information, JDK might already be installed

2. **If JDK is not installed**, the project will install it automatically when you run the application.

#### Option B: Manual Installation
If you prefer to install JDK manually:

1. **Visit the official Adoptium website**:
   - Open your web browser
   - Go to: https://adoptium.net/temurin/releases/

2. **Download JDK 25**:
   - Select **JDK 25** (Latest LTS)
   - Choose **Windows x64 MSI Installer**
   - Click the download button

3. **Run the MSI installer**:
   - Locate the downloaded `.msi` file
   - Right-click and select "Run as administrator"
   - Follow the installation wizard
   - Install to the default location

4. **Verify installation**:
   - Open Command Prompt
   - Type: `java -version`
   - You should see: `openjdk version "25.0.x"`

---

### Step 3: Download JavaFX 25 SDK

1. **Visit the official JavaFX website**:
   - Open your web browser
   - Go to: https://gluonhq.com/products/javafx/

2. **Download JavaFX 25 SDK**:
   - Scroll down to "Download JavaFX"
   - Select **JavaFX Windows SDK** (zip file)
   - Download the latest version (25.x.x)

3. **Extract JavaFX SDK**:
   - Locate the downloaded zip file
   - Right-click and select "Extract All..."
   - Extract to: `Practica-II\Java\configurations\javafx-25-sdk`
   - The folder structure should look like:
     ```
     Practica-II/
     └── Java/
         └── configurations/
             └── javafx-25-sdk/
                 ├── lib/
                 ├── src.zip
                 └── ...
     ```

---

### Step 4: Run the Application

#### Method 1: Using Batch File (Recommended)

1. **Navigate to the configurations folder**:
   - Open File Explorer
   - Go to: `Practica-II\Java\configurations\`

2. **Run the batch file**:
   - Double-click `run.bat`
   - Or right-click `run.bat` and select "Run as administrator"

3. **Wait for the process to complete**:
   - The script will check for JDK installation
   - If JDK is missing, it will install it automatically
   - The application will compile and run

#### Method 2: Using PowerShell

1. **Open PowerShell as Administrator**:
   - Press `Win + X`
   - Select "Windows PowerShell (Admin)"

2. **Navigate to the project**:
   ```powershell
   cd "C:\Path\To\Your\Practica-II\Java\configurations"
   ```

3. **Run the PowerShell script**:
   ```powershell
   .\run.ps1
   ```

---

### Step 5: Verify Everything Works

When the application runs successfully, you should see:

```
Using JDK: C:\Program Files\Eclipse Adoptium\jdk-25.x.x-hotspot

Compiling Main.java...

[OK] Compilation successful!

Running Main...
```

A JavaFX window should open showing the course management application.

---

## 📁 Project Structure Explained

```
Practica-II/
├── Java/                          # Main JavaFX Application
│   ├── Main.java                 # Application entry point
│   └── configurations/           # Build and runtime configuration
│       ├── run.bat              # Windows batch runner (main script)
│       ├── run.ps1              # PowerShell runner
│       ├── build.gradle         # Gradle build configuration
│       ├── pom.xml              # Maven build configuration
│       ├── javafx-25-sdk/       # JavaFX libraries (you need to add this)
│       │   └── lib/            # JavaFX JAR files
│       └── jdk-local/          # Optional: Place JDK MSI here for distribution
│
├── SQL/                          # Database related files
│   ├── Python-Insert-Scripts/   # Python scripts to populate database
│   │   ├── cursuri.py          # Course data insertion
│   │   ├── profesori.py        # Professor data insertion
│   │   ├── studenti.py         # Student data insertion
│   │   └── ...
│   └── SQL Demo Files/         # SQL schema and sample data
│       ├── All-in-One/         # Complete database setup
│       └── Separate/           # Individual table scripts
│
└── z-Others/                    # Additional resources
    └── bibliografie.txt        # Bibliography/references
```

---

## 🔧 Troubleshooting Guide

### Problem: "JDK 25 installation failed"

**Solution**:
1. Ensure you have administrator privileges
2. Check if antivirus software is blocking the installation
3. Try downloading the JDK MSI manually from adoptium.net
4. Install it manually using the MSI installer

### Problem: "javafx-25-sdk folder not found"

**Solution**:
1. Download JavaFX SDK from gluonhq.com
2. Extract it to `Java\configurations\javafx-25-sdk\`
3. Ensure the `lib` folder contains JAR files

### Problem: "Compilation failed"

**Solution**:
1. Verify JDK 25 is properly installed
2. Check that JAVA_HOME environment variable is set
3. Ensure JavaFX SDK is in the correct location
4. Try running as administrator

### Problem: "Application window doesn't open"

**Solution**:
1. Check if JavaFX runtime is available
2. Ensure all required JAR files are present
3. Try updating your graphics drivers
4. Check Windows Firewall settings

### Problem: "Access denied" or "Permission denied"

**Solution**:
1. Right-click the batch file or PowerShell
2. Select "Run as administrator"
3. If using PowerShell, ensure execution policy allows scripts:
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

---

## 📚 Additional Resources

### Database Setup
The project includes SQL scripts to set up the database:
- Navigate to `SQL\SQL Demo Files\All-in-One\`
- Run the SQL scripts in your preferred database management tool

### Python Data Insertion
To populate the database with sample data:
1. Install Python 3.x
2. Navigate to `SQL\Python-Insert-Scripts\`
3. Run the Python scripts: `python cursuri.py`, etc.

### Development
- **IDE Recommendation**: Use Visual Studio Code or IntelliJ IDEA
- **Build Tools**: Gradle (build.gradle) or Maven (pom.xml) configurations included
- **Version Control**: The project can be used with Git

---

## ❓ Frequently Asked Questions

**Q: Can I use JDK 17 or 21 instead of JDK 25?**
A: No, this project specifically requires JDK 25 and JavaFX 25 for compatibility.

**Q: Do I need to install JavaFX separately?**
A: Yes, JavaFX is not included with JDK 25 and must be downloaded separately.

**Q: Can I run this on Windows 11?**
A: Yes, the instructions work for both Windows 10 and Windows 11.

**Q: How do I update the JDK?**
A: Download a newer JDK 25 MSI from adoptium.net and place it in `Java\configurations\jdk-local\`

**Q: The application runs but looks wrong**
A: Ensure you have the correct JavaFX version (25.x.x) and all JAR files are present.

---

## 📞 Support

If you encounter issues not covered in this guide:

1. Check the troubleshooting section above
2. Verify all prerequisites are met
3. Try running with administrator privileges
4. Check the command output for specific error messages

For additional help, consult your course instructor or teaching assistant.

---

**Last Updated**: May 14, 2026
**Project Version**: Practica Anul II - Variant 16
    └── bibliografie.txt       # Bibliography
```

## Technologies Used

- **Java 25** - Programming language
- **JavaFX 25** - GUI framework
- **MySQL** - Database
- **Python** - Database scripting

## Common Issues & Solutions

### Issue: "JAVA_HOME is not set"
**Solution**: Install JDK 25 and set JAVA_HOME environment variable (see SETUP_GUIDE.md)

### Issue: "JavaFX SDK not found"
**Solution**: Download JavaFX 25 SDK and extract to `Java\configurations\javafx-25-sdk\` (see SETUP_GUIDE.md)

### Issue: Compilation or runtime errors
**Solution**: Check SETUP_GUIDE.md Troubleshooting section for detailed solutions

## Troubleshooting

For comprehensive troubleshooting and FAQs, see: **[SETUP_GUIDE.md - Troubleshooting](SETUP_GUIDE.md#troubleshooting)**

## Database

The SQL folder contains:
- Database schema and structure scripts
- Insert scripts for sample data (courses, professors, lessons, students)
- Python scripts for automated data insertion

## Build & Run Alternatives

### Maven
```bash
cd Java\configurations
mvn clean javafx:run
```

### Gradle
```bash
cd Java\configurations
gradle run
```

## Requirements

- Windows 10/11 (or WSL)
- JDK 25 or later
- JavaFX 25 SDK
- 500MB free disk space

## Last Updated

May 2026

---

**For complete setup instructions, see: [SETUP_GUIDE.md](SETUP_GUIDE.md)**