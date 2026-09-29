package hospital.turnos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Parte 2: dobles de prueba frente a la API externa de pagos. */
class ServicioCobroTurnoTest {

    private final CalculadoraCopago calculadora = new CalculadoraCopago();

    // --- Stubs: verifican el ESTADO resultante ---

    @Test
    @DisplayName("Stub: si la pasarela rechaza el pago, el turno queda pendiente")
    void pagoRechazadoDejaElTurnoPendiente() {
        // Arrange
        PasarelaPagos pasarela = new PasarelaPagosStub(ResultadoPago.RECHAZADO);
        ServicioCobroTurno servicio = new ServicioCobroTurno(pasarela, calculadora);
        Turno turno = new Turno("30111222", 40, ObraSocial.SWISS, false);

        // Act
        EstadoTurno estado = servicio.confirmar(turno);

        // Assert
        assertEquals(EstadoTurno.PENDIENTE_DE_PAGO, estado);
    }

    @Test
    @DisplayName("Stub: si la pasarela aprueba el pago, el turno queda confirmado")
    void pagoAprobadoConfirmaElTurno() {
        // Arrange
        PasarelaPagos pasarela = new PasarelaPagosStub(ResultadoPago.APROBADO);
        ServicioCobroTurno servicio = new ServicioCobroTurno(pasarela, calculadora);
        Turno turno = new Turno("30111222", 40, ObraSocial.SWISS, false);

        // Act
        EstadoTurno estado = servicio.confirmar(turno);

        // Assert
        assertEquals(EstadoTurno.CONFIRMADO, estado);
    }

    @Test
    @DisplayName("Stub: si la pasarela se cae, el turno queda pendiente sin romper")
    void pasarelaCaidaDejaElTurnoPendiente() {
        // Arrange
        PasarelaPagos pasarelaCaida =
                (dni, importe) -> {
                    throw new IllegalStateException("Timeout de la pasarela");
                };
        ServicioCobroTurno servicio = new ServicioCobroTurno(pasarelaCaida, calculadora);
        Turno turno = new Turno("30111222", 40, ObraSocial.PUBLICA, false);

        // Act
        EstadoTurno estado = servicio.confirmar(turno);

        // Assert
        assertEquals(EstadoTurno.PENDIENTE_DE_PAGO, estado);
    }

    // --- Mocks: verifican la INTERACCION con la pasarela ---

    @Test
    @DisplayName("Mock: cobra una vez, al paciente y por el importe correctos")
    void cobraUnaVezConElImporteCorrecto() {
        // Arrange
        PasarelaPagos pasarela = mock(PasarelaPagos.class);
        when(pasarela.cobrar("30111222", 2_000.0)).thenReturn(ResultadoPago.APROBADO);
        ServicioCobroTurno servicio = new ServicioCobroTurno(pasarela, calculadora);
        Turno turno = new Turno("30111222", 40, ObraSocial.SWISS, false);

        // Act
        servicio.confirmar(turno);

        // Assert
        verify(pasarela, times(1)).cobrar("30111222", 2_000.0);
        verifyNoMoreInteractions(pasarela);
    }

    @Test
    @DisplayName("Mock: una urgencia se confirma sin llamar nunca a la pasarela")
    void urgenciaNoLlamaALaPasarela() {
        // Arrange
        PasarelaPagos pasarela = mock(PasarelaPagos.class);
        ServicioCobroTurno servicio = new ServicioCobroTurno(pasarela, calculadora);
        Turno urgencia = new Turno("30111222", 40, ObraSocial.PARTICULAR, true);

        // Act
        EstadoTurno estado = servicio.confirmar(urgencia);

        // Assert
        assertEquals(EstadoTurno.CONFIRMADO, estado);
        verify(pasarela, never()).cobrar(anyString(), anyDouble());
    }
}
