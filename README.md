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

The runner compiles sources recursively from `JavaFX\src` into `JavaFX\build\classes`, then starts `Main`.

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
|-- JavaFX/
|   |-- src/
|   |   |-- 01-pornire-aplicatie/     Main JavaFX application
|   |   |-- 02-mostenire-persoane/    Persoana, Student, Profesor
|   |   |-- 03-mostenire-lectii/      Lectie and lesson subclasses
|   |   |-- 04-interfete/             Exportable, Reportable, Searchable
|   |   |-- 05-enumerari/             NivelCurs, StatusInrolare, TipLectie
|   |   |-- 06-modele-domeniu/        Curs, Inrolare and table row models
|   |   |-- 07-acces-date/            Database, config and DAO classes
|   |   |-- 08-servicii-validare/     Reports, validation and error helpers
|   |-- resources/                    styles.css and db.properties
|   |-- lib/                          mssql-jdbc.jar
|   |-- build/classes/                generated .class files
|   |-- configurations/               run scripts, JavaFX SDK and local JDK
|-- Java/ConsoleApp/
|   |-- src/
|   |   |-- 01-pornire-consola/       Console Main
|   |   |-- 02-modele-domeniu/        Console domain models
|   |   |-- 03-acces-date/            Console database and DAO classes
|   |   |-- 04-servicii-rapoarte/     Console report service
|   |   |-- 05-utilitare/             Console helpers
|-- SQL/                              database schema and sample data scripts
|-- z-Others/                         SQL auth DLL and bibliography
```

## Required Runtime Files

Keep these files/folders:

- `JavaFX\src`
- `JavaFX\resources`
- `JavaFX\lib\mssql-jdbc.jar`
- `JavaFX\configurations\run.bat`
- `JavaFX\configurations\run.ps1`
- `JavaFX\configurations\javafx-25-sdk\lib`
- `JavaFX\configurations\jdk-25`
- `z-Others\mssql-jdbc_auth-13.4.0.x64.dll`
- `SQL\SQL Demo Files\All-in-One\structure-and-inserts-in-order.sql`

Generated files such as `.class`, `JavaFX\build`, `Java\ConsoleApp\build`, and run logs are ignored by Git.

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
