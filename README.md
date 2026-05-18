# Practica II - Cursuri Online

JavaFX desktop application for managing online courses, professors, students, enrollments, reports, and exports with a SQL Server database.

## Run On Windows

From Windows Terminal:

```powershell
cd C:\Users\PC\Desktop\Practica-II
.\JavaFX\configurations\run.bat
```

PowerShell alternative:

```powershell
cd C:\Users\PC\Desktop\Practica-II
.\JavaFX\configurations\run.ps1
```

The runner compiles sources from `JavaFX\src` into `JavaFX\build\classes`, then starts `Main`.

## Database Configuration

The app reads SQL Server settings from:

```text
JavaFX\resources\db.properties
```

Default example:

```properties
db.server=localhost:1433
db.name=Cursuri_Online
db.integratedSecurity=true
db.user=
db.password=
```

For Windows Authentication, keep `db.integratedSecurity=true`. For SQL Login, set it to `false` and fill `db.user` plus `db.password`.

The JDBC URL enables Unicode string parameters, so new values saved from the app keep Romanian diacritics.

## Project Structure

```text
Practica-II/
├── JavaFX/
│   ├── src/                     Java source files
│   ├── resources/               styles.css and db.properties
│   ├── lib/                     mssql-jdbc.jar
│   ├── build/classes/           generated .class files
│   └── configurations/          run scripts, Maven/Gradle files, JavaFX SDK
├── SQL/                         database schema and sample data scripts
└── z-Others/                    SQL auth DLL and bibliography
```

## Required Runtime Files

Keep these files/folders:

- `JavaFX\src`
- `JavaFX\resources`
- `JavaFX\lib\mssql-jdbc.jar`
- `JavaFX\configurations\run.bat`
- `JavaFX\configurations\run.ps1`
- `JavaFX\configurations\javafx-25-sdk\lib`
- `z-Others\mssql-jdbc_auth-13.4.0.x64.dll`
- `SQL\SQL Demo Files\All-in-One\structure-and-inserts-in-order.sql`

Generated files such as `.class`, `JavaFX\build`, and run logs are ignored by Git.

## Database Setup

1. Start SQL Server.
2. Create the `Cursuri_Online` database.
3. Run:

```text
SQL\SQL Demo Files\All-in-One\structure-and-inserts-in-order.sql
```

If your server/instance is different, edit `JavaFX\resources\db.properties`.

## Notes

- The full Microsoft JDBC distribution is not required. The app only needs `JavaFX\lib\mssql-jdbc.jar` and the auth DLL in `z-Others`.
- The app repairs common broken Romanian diacritics when reading existing database values, but characters already saved as `?` cannot always be reconstructed perfectly.
