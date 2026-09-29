package hospital.turnos;

import java.util.Arrays;

/**
 * Un turno tal como lo guarda el sistema heredado: "Ana Perez-34-OSDE-URGENTE". Centraliza el
 * parseo que antes estaba repetido en cada formato del reporte.
 */
record RegistroTurno(String paciente, String edad, String obraSocial, boolean esUrgente) {

    private static final String SEPARADOR = "-";
    private static final String MARCA_URGENTE = "URGENTE";
    private static final int CAMPOS_CON_URGENCIA = 4;

    static RegistroTurno desde(String texto) {
        String[] campos = texto.split(SEPARADOR);
        boolean urgente = campos.length == CAMPOS_CON_URGENCIA && MARCA_URGENTE.equals(campos[3]);
        return new RegistroTurno(campos[0], campos[1], campos[2], urgente);
    }

    boolean esPremium() {
        return Arrays.stream(ObraSocial.values())
                .anyMatch(obra -> obra.esPremium() && obra.name().equals(obraSocial));
    }

    String descripcion() {
        return paciente + " (" + edad + " anios) - " + obraSocial;
    }
}
