package hospital.turnos;

/**
 * Confirma un turno cobrando el copago a traves de la pasarela externa. Si el turno no tiene
 * copago, no se llama a la pasarela. Si la pasarela rechaza o falla, el turno queda pendiente de
 * pago en lugar de romper la operacion.
 */
public class ServicioCobroTurno {

    private final PasarelaPagos pasarela;
    private final CalculadoraCopago calculadora;

    public ServicioCobroTurno(PasarelaPagos pasarela, CalculadoraCopago calculadora) {
        this.pasarela = pasarela;
        this.calculadora = calculadora;
    }

    public EstadoTurno confirmar(Turno turno) {
        double copago = calculadora.calcular(turno);

        if (copago == 0.0) {
            return EstadoTurno.CONFIRMADO;
        }

        try {
            ResultadoPago resultado = pasarela.cobrar(turno.dniPaciente(), copago);
            return resultado == ResultadoPago.APROBADO
                    ? EstadoTurno.CONFIRMADO
                    : EstadoTurno.PENDIENTE_DE_PAGO;
        } catch (RuntimeException falloDeLaPasarela) {
            return EstadoTurno.PENDIENTE_DE_PAGO;
        }
    }
}
