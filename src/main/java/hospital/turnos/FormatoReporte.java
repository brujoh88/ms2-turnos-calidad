package hospital.turnos;

import java.util.List;
import java.util.Optional;

/** Estrategia de salida del reporte: cada formato define su encabezado, sus lineas y su pie. */
enum FormatoReporte {
    TXT {
        @Override
        String encabezado(String titulo) {
            return titulo + "\n";
        }

        @Override
        String linea(RegistroTurno turno) {
            return turno.descripcion() + "\n";
        }
    },
    CSV {
        @Override
        String encabezado(String titulo) {
            return "paciente;edad;obra_social\n";
        }

        @Override
        String linea(RegistroTurno turno) {
            return String.join(";", turno.paciente(), turno.edad(), turno.obraSocial()) + "\n";
        }
    },
    HTML {
        @Override
        String encabezado(String titulo) {
            return "<h1>" + titulo + "</h1>\n<ul>\n";
        }

        @Override
        String linea(RegistroTurno turno) {
            return "<li>" + turno.descripcion() + "</li>\n";
        }

        @Override
        String pie() {
            return "</ul>\n";
        }
    };

    abstract String encabezado(String titulo);

    abstract String linea(RegistroTurno turno);

    String pie() {
        return "";
    }

    String renderizar(String titulo, List<RegistroTurno> turnos) {
        StringBuilder salida = new StringBuilder(encabezado(titulo));
        turnos.forEach(turno -> salida.append(linea(turno)));
        return salida.append(pie()).toString();
    }

    static Optional<FormatoReporte> desde(String nombre) {
        for (FormatoReporte formato : values()) {
            if (formato.name().equals(nombre)) {
                return Optional.of(formato);
            }
        }
        return Optional.empty();
    }
}
