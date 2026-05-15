public abstract class Persoana {
    private final int id;
    private final String nume;
    private final String email;
    private final String parola;

    public Persoana(int id, String nume, String email, String parola) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.parola = parola;
    }

    public int getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public boolean valideaza() {
        return email != null && email.contains("@") && parola != null && parola.length() >= 6;
    }

    @Override
    public abstract String toString();
}
