package hospital.turnos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

/**
 * Tests de caracterizacion: fijan la salida actual del reporte heredado antes de refactorizarlo. Si
 * el refactor cambia un solo caracter de la salida, fallan.
 */
class ReporteTurnosTest {

    private static final String HOSPITAL = "Hospital Central";

    private static final List<String> TURNOS =
            List.of(
                    "Ana Perez-34-OSDE-URGENTE",
                    "Carlos Diaz-70-PUBLICA",
                    "Lucia Gomez-45-SWISS",
                    "Marta Ruiz-80-PUBLICA-URGENTE");

    private final ReporteTurnos reporte = new ReporteTurnos();

    static Stream<Arguments> salidasDeTodos() {
        return Stream.of(
                Arguments.of(
                        "TODOS",
                        "TXT",
                        """
                        Hospital Central - Todos los turnos
                        Ana Perez (34 anios) - OSDE
                        Carlos Diaz (70 anios) - PUBLICA
                        Lucia Gomez (45 anios) - SWISS
                        Marta Ruiz (80 anios) - PUBLICA
                        """),
                Arguments.of(
                        "TODOS",
                        "CSV",
                        """
                        paciente;edad;obra_social
                        Ana Perez;34;OSDE
                        Carlos Diaz;70;PUBLICA
                        Lucia Gomez;45;SWISS
                        Marta Ruiz;80;PUBLICA
                        """),
                Arguments.of(
                        "TODOS",
                        "HTML",
                        """
                        <h1>Hospital Central - Todos los turnos</h1>
                        <ul>
                        <li>Ana Perez (34 anios) - OSDE</li>
                        <li>Carlos Diaz (70 anios) - PUBLICA</li>
                        <li>Lucia Gomez (45 anios) - SWISS</li>
                        <li>Marta Ruiz (80 anios) - PUBLICA</li>
                        </ul>
                        """));
    }

    static Stream<Arguments> salidasDeUrgentes() {
        return Stream.of(
                Arguments.of(
                        "URGENTES",
                        "TXT",
                        """
                        Hospital Central - Turnos urgentes
                        Ana Perez (34 anios) - OSDE
                        Marta Ruiz (80 anios) - PUBLICA
                        """),
                Arguments.of(
                        "URGENTES",
                        "CSV",
                        """
                        paciente;edad;obra_social
                        Ana Perez;34;OSDE
                        Marta Ruiz;80;PUBLICA
                        """),
                Arguments.of(
                        "URGENTES",
                        "HTML",
                        """
                        <h1>Hospital Central - Turnos urgentes</h1>
                        <ul>
                        <li>Ana Perez (34 anios) - OSDE</li>
                        <li>Marta Ruiz (80 anios) - PUBLICA</li>
                        </ul>
                        """));
    }

    static Stream<Arguments> salidasDePremium() {
        return Stream.of(
                Arguments.of(
                        "PREMIUM",
                        "TXT",
                        """
                        Hospital Central - Turnos de obras sociales premium
                        Ana Perez (34 anios) - OSDE
                        Lucia Gomez (45 anios) - SWISS
                        """),
                Arguments.of(
                        "PREMIUM",
                        "CSV",
                        """
                        paciente;edad;obra_social
                        Ana Perez;34;OSDE
                        Lucia Gomez;45;SWISS
                        """),
                Arguments.of(
                        "PREMIUM",
                        "HTML",
                        """
                        <h1>Hospital Central - Turnos de obras sociales premium</h1>
                        <ul>
                        <li>Ana Perez (34 anios) - OSDE</li>
                        <li>Lucia Gomez (45 anios) - SWISS</li>
                        </ul>
                        """));
    }

    @ParameterizedTest(name = "{0} en {1}")
    @MethodSource({"salidasDeTodos", "salidasDeUrgentes", "salidasDePremium"})
    @DisplayName("Caracterizacion: la salida de cada tipo y formato no cambia")
    void salidaDelReporte(String tipo, String formato, String esperado) {
        // Arrange: TURNOS y HOSPITAL

        // Act
        String salida = reporte.generar(TURNOS, tipo, formato, HOSPITAL);

        // Assert
        assertEquals(esperado, salida);
    }

    @Test
    @DisplayName("Seguridad: un nombre de hospital con ${...} se muestra tal cual, sin evaluarse")
    void nombreDeHospitalNoSeInterpreta() {
        // Arrange
        String hospitalMalicioso = "Hospital ${sys:user.name} ${script:javascript:1+1}";

        // Act
        String salida = reporte.generar(List.of(), "TODOS", "TXT", hospitalMalicioso);

        // Assert
        assertEquals(hospitalMalicioso + " - Todos los turnos\n", salida);
    }

    @Test
    @DisplayName("Un tipo de reporte desconocido se trata como TODOS")
    void tipoDesconocidoIncluyeTodosLosTurnos() {
        // Act
        String salida = reporte.generar(TURNOS, "INEXISTENTE", "CSV", HOSPITAL);

        // Assert
        assertEquals(reporte.generar(TURNOS, "TODOS", "CSV", HOSPITAL), salida);
    }

    @Test
    @DisplayName("Un cuarto campo distinto de URGENTE no marca el turno como urgente")
    void cuartoCampoDistintoDeUrgente() {
        // Act
        String salida =
                reporte.generar(List.of("Juan Sosa-50-OSDE-CONTROL"), "URGENTES", "CSV", HOSPITAL);

        // Assert
        assertEquals("paciente;edad;obra_social\n", salida);
    }

    @Test
    @DisplayName("Caracterizacion: una lista nula produce un reporte vacio")
    void listaNulaProduceReporteVacio() {
        // Act
        String salida = reporte.generar(null, "TODOS", "TXT", HOSPITAL);

        // Assert
        assertEquals("", salida);
    }

    @Test
    @DisplayName("Caracterizacion: un formato desconocido produce un reporte vacio")
    void formatoDesconocidoProduceReporteVacio() {
        // Act
        String salida = reporte.generar(TURNOS, "TODOS", "XML", HOSPITAL);

        // Assert
        assertEquals("", salida);
    }
}
