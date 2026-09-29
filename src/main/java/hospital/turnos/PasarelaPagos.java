package hospital.turnos;

/** API externa de pagos. En produccion la implementa el proveedor. */
public interface PasarelaPagos {

    ResultadoPago cobrar(String dniPaciente, double importe);
}
