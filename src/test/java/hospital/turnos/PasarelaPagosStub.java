package hospital.turnos;

/**
 * Stub escrito a mano: devuelve siempre la respuesta preconfigurada. No registra nada; solo le da
 * al servicio la entrada indirecta que necesita.
 */
class PasarelaPagosStub implements PasarelaPagos {

    private final ResultadoPago respuestaEnlatada;

    PasarelaPagosStub(ResultadoPago respuestaEnlatada) {
        this.respuestaEnlatada = respuestaEnlatada;
    }

    @Override
    public ResultadoPago cobrar(String dniPaciente, double importe) {
        return respuestaEnlatada;
    }
}
