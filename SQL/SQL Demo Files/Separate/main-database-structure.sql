-- Script SQL pentru crearea structurii bazei de date "Cursuri_Online"
CREATE DATABASE Cursuri_Online;
USE Cursuri_Online;

-- Tabelul de domenii, care conține informații despre fiecare domeniu de studiu
CREATE TABLE Domenii(
	DomeniuID INT PRIMARY KEY,
	DenumireDomeniu NVARCHAR(100) NOT NULL,
);

-- Tabelul de raioane, care conține informații despre fiecare raion din țară
CREATE TABLE Raioane(
	RaionID INT PRIMARY KEY,
	DenumireRaion NVARCHAR(100) NOT NULL,
);

-- Tabelul de localități, care conține informații despre fiecare localitate și raionul în care se află
CREATE TABLE Localitati(
	LocalitateID INT PRIMARY KEY,
	DenumireLocalitate NVARCHAR(100) NOT NULL,
	RaionID INT REFERENCES Raioane(RaionID),
);

-- Tabelul de instituții, care conține informații despre fiecare instituție
CREATE TABLE Institutii(
	InstitutieID INT PRIMARY KEY,
	DenumireInstitutie NVARCHAR(100) NOT NULL,
	TipInstitutie NCHAR(7) CHECK (TipInstitutie IN ('Privată', 'Publică')),
	Email VARCHAR(100),
	Website VARCHAR(100),
	DirectorInstitutie NVARCHAR(100),
	LocalitateID INT REFERENCES Localitati(LocalitateID),
);

-- Tabelul de profesori, care conține informații despre fiecare profesor angajat de instituții
CREATE TABLE Profesori(
	ProfesorID INT PRIMARY KEY,
	IDNP CHAR(13) NOT NULL UNIQUE,
	NumeProfesor NVARCHAR(50),
	PrenumeProfesor NVARCHAR(50),
	PatronimicProfesor NVARCHAR(50),
	DataNasterii DATE,
	SexProfesor CHAR(1) CHECK (SexProfesor IN ('M', 'F')),
	NrTelefon CHAR(12),
	Email VARCHAR(100) UNIQUE,
	TipCertificare NVARCHAR(30) CHECK (TipCertificare IN ('Făra grad didactic', 'Grad didactic II', 'Grad didactic I', 'Grad didactic superior')),
	DataAngajarii DATE,
	InstitutieID INT REFERENCES Institutii(InstitutieID),
);

-- Tabelul de cursuri, care conține informații despre fiecare curs oferit de instituții
CREATE TABLE Cursuri(
	CursID INT PRIMARY KEY,
	DenumireCurs NVARCHAR(100) NOT NULL,
	LimbaPredare NVARCHAR(10) CHECK (LimbaPredare IN ('Română', 'Engleză', 'Rusă', 'Franceză')),
	TipPredare NVARCHAR(10) CHECK (TipPredare IN ('Online', 'Offline', 'Hibrid')),
	PretCurs DECIMAL(10, 2) CHECK (PretCurs >= 0),
	Coordonator NVARCHAR(100) UNIQUE,
	DomeniuID INT REFERENCES Domenii(DomeniuID),
	InstitutieID INT REFERENCES Institutii(InstitutieID),
);

-- Tabelul de grupe, care conține informații despre fiecare grupă participantă la cursuri
CREATE TABLE Grupe_Cursuri(
	GrupaID INT PRIMARY KEY,
	NumeGrupa VARCHAR(20) NOT NULL,
	Capacitate INT CHECK (Capacitate <= 50 AND Capacitate > 0),
	CursID INT REFERENCES Cursuri(CursID),
);

-- Tabelul de lecții, care conține informații despre fiecare lecție din cadrul grupelor de cursuri
CREATE TABLE Lectii(
	LectieID INT PRIMARY KEY,
	GrupaID INT REFERENCES Grupe_Cursuri(GrupaID),
	ProfesorID INT REFERENCES Profesori(ProfesorID),
	DataLectie DATETIME DEFAULT GETDATE(),
	DurataLectie DECIMAL(5, 2) DEFAULT 1.5,
);

-- Tabelul de studenți, care conține informații despre fiecare student
CREATE TABLE Studenti(
	StudentID INT PRIMARY KEY,
	IDNP CHAR(13) NOT NULL UNIQUE,
	NumeStudent NVARCHAR(50),
	PrenumeStudent NVARCHAR(50),
	PatronimicStudent NVARCHAR(50),
	DataNasterii DATE,
	SexStudent CHAR(1) CHECK (SexStudent IN ('M', 'F')),
	NrTelefon CHAR(12),
	LocalitateID INT REFERENCES Localitati(LocalitateID),
);

-- Tabelul de participări la cursuri, care leagă studenții de grupele de cursuri
CREATE TABLE Participari_Cursuri(
	ParticipareID INT PRIMARY KEY,
	StudentID INT REFERENCES Studenti(StudentID) NOT NULL,
	GrupaID INT REFERENCES Grupe_Cursuri(GrupaID) NOT NULL,
);

