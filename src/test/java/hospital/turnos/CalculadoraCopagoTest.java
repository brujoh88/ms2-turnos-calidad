package hospital.turnos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Parte 1: pruebas unitarias con el patron AAA. */
class CalculadoraCopagoTest {

    private static final double DELTA = 0.001;

    private final CalculadoraCopago calculadora = new CalculadoraCopago();

    // --- Casos de la condicion de exencion (Parte 3: cobertura de ramas) ---

    @Test
    @Tag("sentencias")
    @DisplayName("Caso 1: una urgencia no paga copago")
    void urgenciaNoPagaCopago() {
        // Arrange
        Turno turno = new Turno("30111222", 40, ObraSocial.PARTICULAR, true);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(0.0, copago, DELTA);
    }

    @Test
    @DisplayName("Caso 2: jubilado con obra social premium no paga copago")
    void jubiladoPremiumNoPagaCopago() {
        // Arrange
        Turno turno = new Turno("30111222", 70, ObraSocial.OSDE, false);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(0.0, copago, DELTA);
    }

    @Test
    @DisplayName("Caso 3: premium menor de 65 paga el 20 %")
    void premiumNoJubiladoPagaVeintePorCiento() {
        // Arrange
        Turno turno = new Turno("30111222", 40, ObraSocial.SWISS, false);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(2_000.0, copago, DELTA);
    }

    @Test
    @Tag("sentencias")
    @DisplayName("Caso 4: obra social no premium, sin urgencia, paga el 50 %")
    void publicaPagaCincuentaPorCiento() {
        // Arrange
        Turno turno = new Turno("30111222", 70, ObraSocial.PUBLICA, false);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(5_000.0, copago, DELTA);
    }

    // --- Resto de las reglas ---

    @Test
    @DisplayName("Un particular paga el arancel completo")
    void particularPagaArancelCompleto() {
        // Arrange
        Turno turno = new Turno("30111222", 30, ObraSocial.PARTICULAR, false);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(CalculadoraCopago.ARANCEL_CONSULTA, copago, DELTA);
    }

    // --- Casos de borde ---

    @Test
    @DisplayName("Borde: con 64 anios un premium todavia paga")
    void premiumDeSesentaYCuatroPaga() {
        // Arrange
        Turno turno = new Turno("30111222", 64, ObraSocial.OSDE, false);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(2_000.0, copago, DELTA);
    }

    @Test
    @DisplayName("Borde: con 65 anios justos un premium ya no paga")
    void premiumDeSesentaYCincoNoPaga() {
        // Arrange
        Turno turno = new Turno("30111222", 65, ObraSocial.OSDE, false);

        // Act
        double copago = calculadora.calcular(turno);

        // Assert
        assertEquals(0.0, copago, DELTA);
    }

    @Test
    @DisplayName("Borde: obra social nula se rechaza con excepcion controlada")
    void obraSocialNulaSeRechaza() {
        // Arrange / Act
        IllegalArgumentException error =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new Turno("30111222", 40, null, false));

        // Assert
        assertTrue(error.getMessage().contains("obra social"));
    }

    @Test
    @DisplayName("Borde: una edad de cero se rechaza")
    void edadCeroSeRechaza() {
        // Arrange / Act / Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> new Turno("30111222", 0, ObraSocial.OSDE, false));
    }

    @Test
    @DisplayName("Borde: un DNI nulo se rechaza")
    void dniNuloSeRechaza() {
        // Arrange / Act / Assert
        assertThrows(
                IllegalArgumentException.class, () -> new Turno(null, 40, ObraSocial.OSDE, false));
    }

    @Test
    @DisplayName("Borde: un DNI vacio se rechaza")
    void dniVacioSeRechaza() {
        // Arrange / Act / Assert
        assertThrows(
                IllegalArgumentException.class, () -> new Turno("   ", 40, ObraSocial.OSDE, false));
    }
}
