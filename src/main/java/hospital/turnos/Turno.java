package hospital.turnos;

/**
 * Datos de un turno. Valida en la construccion para que nunca exista un turno invalido: es la
 * correccion del NullPointerException que tenia el codigo original cuando la obra social llegaba en
 * null.
 */
public record Turno(String dniPaciente, int edad, ObraSocial obraSocial, boolean esUrgente) {

    public Turno {
        if (dniPaciente == null || dniPaciente.isBlank()) {
            throw new IllegalArgumentException("El DNI del paciente es obligatorio");
        }
        if (edad <= 0) {
            throw new IllegalArgumentException("La edad debe ser mayor a cero: " + edad);
        }
        if (obraSocial == null) {
            throw new IllegalArgumentException("La obra social es obligatoria");
        }
    }
}
