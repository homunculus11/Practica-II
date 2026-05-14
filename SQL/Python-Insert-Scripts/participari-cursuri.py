import random


def generate_insert_statements(start_id, end_id, batch_size=1000):
    if start_id > end_id:
        raise ValueError("start_id must be less than or equal to end_id")

    rows = []
    for participare_id in range(start_id, end_id + 1):
        student_id = random.randint(1, 20000)
        grupa_id = random.randint(1, 5000)
        rows.append((participare_id, student_id, grupa_id))

    statements = []
    for i in range(0, len(rows), batch_size):
        batch = rows[i : i + batch_size]
        values = []
        for row in batch:
            values.append(f"({row[0]}, {row[1]}, {row[2]})")
        statement = "INSERT INTO Participari_Cursuri (ParticipareID, StudentID, GrupaID) VALUES\n"
        statement += ",\n".join("    " + value for value in values) + ";"
        statements.append(statement)

    return statements


def main():
    print("Generate INSERT statements for the Participari_Cursuri table.")
    start_id = int(input("Enter starting ParticipareID: ").strip())
    end_id = int(input("Enter ending ParticipareID: ").strip())
    batch_size = 1000

    statements = generate_insert_statements(start_id, end_id, batch_size)

    for index, statement in enumerate(statements, start=1):
        print(statement)
        if index < len(statements):
            print()


if __name__ == "__main__":
    main()
