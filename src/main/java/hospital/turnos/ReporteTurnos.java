package hospital.turnos;

import org.apache.commons.text.StringSubstitutor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Reporte de turnos. Recibe los turnos en el formato de texto heredado ("Ana
 * Perez-34-OSDE-URGENTE") y los presenta filtrados en TXT, CSV o HTML.
 */
public class ReporteTurnos {

    private static final String PLANTILLA_TITULO = "${hospital} - ${titulo}";

    public String generar(List<String> turnos, String tipo, String formato, String hospital) {
        Optional<FormatoReporte> salida = FormatoReporte.desde(formato);
        if (turnos == null || salida.isEmpty()) {
            return "";
        }

        FiltroTurnos filtro = FiltroTurnos.desde(tipo);
        List<RegistroTurno> seleccionados =
                turnos.stream().map(RegistroTurno::desde).filter(filtro::incluye).toList();

        return salida.get().renderizar(titulo(hospital, filtro), seleccionados);
    }

    /**
     * Solo reemplaza las dos variables conocidas. No usa createInterpolator(): un dato externo con
     * "${...}" se muestra tal cual y nunca se evalua (CVE-2022-42889).
     */
    private String titulo(String hospital, FiltroTurnos filtro) {
        Map<String, String> valores =
                Map.of(
                        "hospital", Objects.requireNonNullElse(hospital, ""),
                        "titulo", filtro.titulo());
        return new StringSubstitutor(valores).replace(PLANTILLA_TITULO);
    }
}
