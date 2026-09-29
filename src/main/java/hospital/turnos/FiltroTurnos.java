package hospital.turnos;

import java.util.function.Predicate;

/** Que turnos entran en el reporte y con que titulo. */
enum FiltroTurnos {
    TODOS("Todos los turnos", turno -> true),
    URGENTES("Turnos urgentes", RegistroTurno::esUrgente),
    PREMIUM("Turnos de obras sociales premium", RegistroTurno::esPremium);

    private final String titulo;
    private final Predicate<RegistroTurno> criterio;

    FiltroTurnos(String titulo, Predicate<RegistroTurno> criterio) {
        this.titulo = titulo;
        this.criterio = criterio;
    }

    /** Un tipo desconocido se trata como TODOS, igual que en el reporte heredado. */
    static FiltroTurnos desde(String tipo) {
        for (FiltroTurnos filtro : values()) {
            if (filtro.name().equals(tipo)) {
                return filtro;
            }
        }
        return TODOS;
    }

    String titulo() {
        return titulo;
    }

    boolean incluye(RegistroTurno turno) {
        return criterio.test(turno);
    }
}
