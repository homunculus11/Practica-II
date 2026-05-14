import random
from datetime import datetime, timedelta


def random_data_lectie():
    """Generate a random date between 01.01.1900 and 01.05.2026"""
    start_date = datetime(1900, 1, 1)
    end_date = datetime(2026, 5, 1)
    random_days = random.randint(0, (end_date - start_date).days)
    return start_date + timedelta(days=random_days)


def random_durata_lectie():
    """Generate a random duration from 1 to 8, in increments of 0.5"""
    return random.choice([1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8])


def generate_insert_statements(start_id, end_id, batch_size=1000):
    if start_id > end_id:
        raise ValueError("start_id must be less than or equal to end_id")

    rows = []
    for lectie_id in range(start_id, end_id + 1):
        grupa_id = random.randint(1, 5000)
        profesor_id = random.randint(1, 1000)
        data_lectie = random_data_lectie()
        durata_lectie = random_durata_lectie()
        rows.append((lectie_id, grupa_id, profesor_id, data_lectie, durata_lectie))

    statements = []
    for i in range(0, len(rows), batch_size):
        batch = rows[i : i + batch_size]
        values = []
        for row in batch:
            data_str = row[3].strftime("%Y-%m-%d")
            values.append(
                f"({row[0]}, {row[1]}, {row[2]}, '{data_str}', {row[4]})"
            )
        statement = "INSERT INTO Lectii (LectieID, GrupaID, ProfesorID, DataLectie, DurataLectie) VALUES\n"
        statement += ",\n".join("    " + value for value in values) + ";"
        statements.append(statement)

    return statements


def main():
    print("Generate INSERT statements for the Lectii table.")
    start_id = int(input("Enter starting LectieID: ").strip())
    end_id = int(input("Enter ending LectieID: ").strip())
    batch_size = 1000

    statements = generate_insert_statements(start_id, end_id, batch_size)

    for index, statement in enumerate(statements, start=1):
        print(statement)
        if index < len(statements):
            print()


if __name__ == "__main__":
    main()
