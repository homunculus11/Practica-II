import random
import datetime

# Add first names and associated sex here
first_names = {
    "Constantin": "M",
    "Nicolae": "M",
    "Dan" : "M",
    "Eduard" : "M",
    "Pavel" : "M",
    "Adriana" : "F",
    "Ion" : "M",
    "Patricia" : "F",
    "Alexandru" : "M",
    "Sergiu" : "M",
    "Vladislav" : "M",
    "Marin" : "M",
    "Salomeia" : "F",
    "Oxana" : "F",
    "Virineea" : "F",
    "Dragoș" : "M",
    "Ecaterina" : "F",
    "Alexandrina" : "F",
    "Alexandra" : "F",
    "Maxim" : "M",
    "Petru" : "M",
    "Remos" : "M",
    "Bogdan" : "M",
    "Gabriel" : "M",
    "Artemie" : "M",
    "Artiom" : "M",
    "Serghei" : "M",
    "Daniel" : "M",
    "Mădălina" : "F",
    "Damian" : "M",
    "Gabriela" : "F",
    "Denis" : "M",
    "Dumitru" : "M",
    "Elvis" : "M",
    "Andrian" : "M",
    "Mihail" : "M",
    "Mihai" : "M",
    "Mihaela" : "F",
    "Mirela" : "F",
    "Valentina" : "F",
    "Valeriu" : "M",
    "Valeria" : "F",
    "Vlad" : "M",
    "Vladimir" : "M",
    "Tudor" : "M",
    "Andrei" : "M",
    "Andreea" : "F",
    "Nicoleta" : "F",
    "Aliona" : "F",
    "Stela" : "F",
    "Ștefan" : "M",
    "Ștefania" : "F",
    "Svetlana" : "F",
    "Diana" : "F",
    "Dorina" : "F",
    "Dorin" : "M",
    "Dorel" : "M",
    "Artur" : "M",
    "Vera" : "F",
    "Viorica" : "F",
    "Veaceslav" : "M",
    "Vasile" : "M",
    "Marcel" : "M",
    "Valentin" : "M",
    "Iulia" : "F",
    "Iulian" : "M",
    "Iuliana" : "F",
    "Violeta" : "F",
    "Ghenadie" : "M",
    "Iulian-Albert" : "M",
    "Octavian" : "M",
    "Sorin" : "M",
    "Marionela" : "F",
    "Nadejda" : "F",
    "Adelina" : "F",
    "Adrian" : "M",
    "Georgiana" : "F",
    "Giorgiana" : "F",
    "Igor" : "M",
    "Anastasia" : "F",
    "Ana-Maria" : "F",
    "Dumitrița" : "F",
    "Robert" : "M",
    "Roxana" : "F",
    "Ruslan" : "M",
    "Eugen" : "M",
    "Eugeniu" : "M",
    "Eugenia" : "F",
    "Evelina" : "F",
    "Fiodor" : "M",
    "Irina" : "F",
    "Loredana" : "F",
    "Tatiana" : "F",
    "Ivan" : "M",
    "Marina" : "F",
    "Marius" : "M",
    "Maria" : "F",
    "Ovidiu" : "M",
    "Oleg" : "M",
    "Olga" : "F",
    "Viorel" : "M",
    "Laurențiu" : "M",
    "Larisa" : "F",
    "Vlada" : "F",
    "Radu" : "M",
    "Catarina" : "F",
    "Daria" : "F",
    "Efimia" : "F",
    "Efim" : "M",
    "Cristina" : "F",
    "Cristian" : "M",
    "Cristi" : "M",
    "Dinuț" : "M",
    "Nichita" : "M",
    "Nikita" : "M",
    "Augustin" : "M",
    "Augustina" : "F",
    "Aurel" : "M",
    "Aurelia" : "F",
    "Liviu" : "M",
    "Laura" : "F",
    "Lavinia" : "F",
    "Liliana" : "F",
    "Alexei" : "M",
    "Alexia" : "F",
    "Alex" : "M",
    "Xenia" : "F",
    "Vadim" : "M",
    "Iaroslav" : "M",
    "Lucian" : "M",
    "Luciana" : "F",
    "Teodor" : "M",
    "Teodora" : "F",
    "Chiril" : "M",
    "Ilie" : "M",
    "Ioana" : "F",
    "Ionela" : "F",
    "Eremia" : "F",
    "Ana" : "F",
    "Beatrice" : "F",
    "Sofia" : "F",
    "Liuba" : "F",
}

# Add surnames here
surnames = [
    "Achiruș",
    "Batițchi",
    "Beșliu",
    "Bragari",
    "Caraman",
    "Ciutac",
    "Cîrlan",
    "Comanac",
    "Corciu",
    "Crețu",
    "Găină",
    "Gojan",
    "Grigoriță",
    "Istrati",
    "Jumiga",
    "Macari",
    "Medinschi",
    "Miron",
    "Mironova",
    "Munteanu",
    "Nedelco",
    "Nicolai",
    "Nitrean",
    "Orlețchi",
    "Pascaru",
    "Poclitar",
    "Postoroncă",
    "Prijilevschi",
    "Roman",
    "Sava",
    "Stepan",
    "Stici",
    "Tilipeț",
    "Arion",
    "Babără",
    "Rusu",
    "Ceban",
    "Ciobanu",
    "Țurcan",
    "Cebotari",
    "Lungu",
    "Sîrbu",
    "Popa",
    "Rotari",
    "Guțu",
    "Ursu",
    "Roșca",
    "Melnic",
    "Bălan",
    "Cojocari",
    "Rotaru",
    "Cojocaru",
    "Grosu",
    "Țurcanu",
    "Moraru",
    "Morari",
    "Muntean",
    "Botnari",
    "Cazacu",
    "Popovici",
    "Ungureanu",
    "Chiriac",
    "Mocanu",
    "Pleșca",
    "Lupu",
    "Plamadeală",
    "Tcaci",
    "Rusnac",
    "Spînu",
    "Florea",
    "Guzun",
    "Sandu",
    "Bivol",
    "Cebanu",
    "Negru",
    "Prodan",
    "Ivanov",
    "Musteață",
    "Luca",
    "Burlacu",
    "Popov",
    "Pînzari",
    "Buga",
    "Lisnic",
    "Gheorghiță",
    "Josan",
    "Arnaut",
    "Bejenari",
    "Oprea",
    "Croitor",
    "Andronic",
    "Pascari",
    "Cușnir",
    "Uzun",
    "Cernei",
    "Bulat",
    "Raileanu",
    "Cazac",
    "Grecu",
    "Golban",
    "Vieru",
    "Ursachi",
    "Postolachi",
    "Vlas",
    "Colesnic",
    "Mardari",
    "Borș",
    "Bostan",
    "Mălai",
    "Frunză",
    "Pascal",
    "Bîrca",
    "Tătaru",
    "Topal",
    "Stratulat",
    "Vîrlan",
    "Radu",
    "Gherman",
    "Brînză",
    "Croitoru",
    "Toma",
    "Vicol",
    "Stratan",
    "Codreanu",
    "Adam",
    "Anghel",
    "Balan",
    "Bancu",
    "Bejan",
    "Filimon",
    "Filip",
    "Gîrlovanu",
    "Gamaniuc",
    "Ghinda",
    "Godoroja",
    "Goraș",
    "Macovei",
    "Manole",
    "Marițoi",
    "Stanciu",
    "Șchiopu",
    "Lazari",
]

certifications = [
    "Făra grad didactic",
    "Grad didactic II",
    "Grad didactic I",
    "Grad didactic superior",
]


def random_date(start_date: datetime.date, end_date: datetime.date) -> datetime.date:
    if start_date > end_date:
        return start_date
    delta = end_date - start_date
    day_offset = random.randint(0, delta.days)
    return start_date + datetime.timedelta(days=day_offset)


def normalize_email_part(value: str) -> str:
    normalized = value.strip().lower().replace(" ", "")
    for ch in "ăâîșțĂÂÎȘȚ":
        normalized = normalized.replace(ch, "a" if ch in "ăâĂÂ" else "i" if ch in "îÎ" else "s" if ch in "șȘ" else "t")
    normalized = ''.join(c for c in normalized if c.isalnum())
    return normalized


def generate_idnp(existing_ids: set[str]) -> str:
    while True:
        suffix = ''.join(str(random.randint(0, 9)) for _ in range(10))
        value = f"200{suffix}"
        if value not in existing_ids:
            existing_ids.add(value)
            return value


def generate_phone() -> str:
    prefix = random.choice(["+37367", "+37369", "+37378", "+37360", "+37368", "+37379"])
    tail = ''.join(str(random.randint(0, 9)) for _ in range(6))
    return prefix + tail


def add_years(source_date: datetime.date, years: int) -> datetime.date:
    try:
        return source_date.replace(year=source_date.year + years)
    except ValueError:
        # Handle leap day by moving to February 28 of the target year
        return source_date.replace(month=2, day=28, year=source_date.year + years)


def generate_profesori_inserts(start_id: int, end_id: int) -> list[str]:
    if start_id > end_id:
        raise ValueError("start_id must be less than or equal to end_id")

    inserts = []
    existing_idnps: set[str] = set()
    birth_start = datetime.date(1920, 1, 1)
    birth_end = datetime.date(2009, 12, 31)

    values = []

    for profesor_id in range(start_id, end_id + 1):
        first_name, sex = random.choice(list(first_names.items()))
        patronymic = random.choice([name for name, s in first_names.items() if s == "M"]) if random.random() > 0.15 else ""
        surname = random.choice(surnames)

        birth_date = random_date(birth_start, birth_end)
        min_hire_date = add_years(birth_date, 18)
        today = datetime.date.today()
        hire_date = random_date(min_hire_date, today)

        idnp = generate_idnp(existing_idnps)
        phone = generate_phone()
        
        # Generate email with 5 variants, each 20% probability
        email_formats = [
            f"{normalize_email_part(first_name)}{normalize_email_part(surname)}@gmail.com",
            f"{normalize_email_part(surname)}{normalize_email_part(first_name)}@gmail.com",
            f"{normalize_email_part(first_name)}-{normalize_email_part(surname)}@gmail.com",
            f"{normalize_email_part(surname)}-{normalize_email_part(first_name)}@gmail.com",
            f"{normalize_email_part(first_name)}{normalize_email_part(surname)}{random.randint(1, 100)}@gmail.com"
        ]
        email = random.choice(email_formats)
        
        certification = random.choice(certifications)
        institutie_id = random.randint(1, 417)

        values.append(
            f"({profesor_id}, '{idnp}', '{surname}', '{first_name}', '{patronymic}', '{birth_date}', '{sex}', '{phone}', '{email}', '{certification}', '{hire_date}', {institutie_id})"
        )

    # Split values into chunks of 1000
    chunk_size = 1000
    insert_statements = []
    
    for i in range(0, len(values), chunk_size):
        chunk = values[i:i + chunk_size]
        insert_statement = (
            "INSERT INTO Profesori (ProfesorID, IDNP, NumeProfesor, PrenumeProfesor, PatronimicProfesor, DataNasterii, SexProfesor, NrTelefon, Email, TipCertificare, DataAngajarii, InstitutieID) VALUES\n"
            + ",\n".join(f"    {value}" for value in chunk)
            + ";"
        )
        insert_statements.append(insert_statement)

    return insert_statements


if __name__ == "__main__":
    start_id = int(input("Start ProfesorID: "))
    end_id = int(input("End ProfesorID: "))

    statements = generate_profesori_inserts(start_id, end_id)
    print("\n\n".join(statements))
