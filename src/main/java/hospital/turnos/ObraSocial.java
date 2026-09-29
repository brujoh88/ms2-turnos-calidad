package hospital.turnos;

public enum ObraSocial {
    OSDE(true),
    SWISS(true),
    PUBLICA(false),
    PARTICULAR(false);

    private final boolean premium;

    ObraSocial(boolean premium) {
        this.premium = premium;
    }

    public boolean esPremium() {
        return premium;
    }
}
