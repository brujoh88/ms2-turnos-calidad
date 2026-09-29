package hospital.turnos;

/**
 * Calcula cuanto paga el paciente por el turno.
 *
 * <p>Reglas: - Las urgencias no pagan copago. - Los jubilados (65 o mas) con obra social premium no
 * pagan copago. - Obra social premium: 20 % del arancel. - Obra social publica: 50 % del arancel. -
 * Particular: arancel completo.
 */
public class CalculadoraCopago {

    public static final double ARANCEL_CONSULTA = 10_000.0;
    static final int EDAD_JUBILATORIA = 65;

    public double calcular(Turno turno) {
        if (estaExentoDeCopago(turno)) {
            return 0.0;
        }

        return ARANCEL_CONSULTA * porcentajeACargoDelPaciente(turno.obraSocial());
    }

    /** El segmento con la condicion compuesta que se analiza en la Parte 3. */
    private boolean estaExentoDeCopago(Turno turno) {
        return turno.esUrgente()
                || (turno.obraSocial().esPremium() && turno.edad() >= EDAD_JUBILATORIA);
    }

    private double porcentajeACargoDelPaciente(ObraSocial obraSocial) {
        return switch (obraSocial) {
            case OSDE, SWISS -> 0.20;
            case PUBLICA -> 0.50;
            case PARTICULAR -> 1.00;
        };
    }
}
