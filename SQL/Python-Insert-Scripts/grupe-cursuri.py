import random
import string


def random_nume_grupa():
    letters = ''.join(random.choices(string.ascii_uppercase, k=2))
    digits = ''.join(random.choices(string.digits, k=4))
    return f"{letters}-{digits}"


def random_capacitate():
    return random.choice([5, 10, 15, 20, 25, 30, 35, 40, 45, 50])


def generate_insert_statements(start_id, end_id, batch_size=1000):
    if start_id > end_id:
        raise ValueError("start_id must be less than or equal to end_id")

    rows = []
    for grupa_id in range(start_id, end_id + 1):
        nume_grupa = random_nume_grupa()
        capacitate = random_capacitate()
        curs_id = random.randint(1, 500)
        rows.append((grupa_id, nume_grupa, capacitate, curs_id))

    statements = []
    for i in range(0, len(rows), batch_size):
        batch = rows[i : i + batch_size]
        values = []
        for row in batch:
            values.append(
                f"({row[0]}, '{row[1]}', {row[2]}, {row[3]})"
            )
        statement = "INSERT INTO Grupe_Cursuri (GrupaID, NumeGrupa, Capacitate, CursID) VALUES\n"
        statement += ",\n".join("    " + value for value in values) + ";"
        statements.append(statement)

    return statements


def main():
    print("Generate INSERT statements for the Grupe_Cursuri table.")
    start_id = int(input("Enter starting GrupaID: ").strip())
    end_id = int(input("Enter ending GrupaID: ").strip())
    batch_size = 1000

    statements = generate_insert_statements(start_id, end_id, batch_size)

    for index, statement in enumerate(statements, start=1):
        print(statement)
        if index < len(statements):
            print()


if __name__ == "__main__":
    main()
