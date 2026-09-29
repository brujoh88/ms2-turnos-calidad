package hospital.turnos;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reporte de turnos heredado del sistema anterior. Recibe los turnos en el formato de texto
 * original: "Ana Perez-34-OSDE-URGENTE".
 */
public class ReporteTurnos {

    public String generar(List<String> turnos, String tipo, String formato, String hospital) {
        String salida = "";
        if (turnos != null) {
            Map<String, String> valores = new HashMap<>();
            valores.put("hospital", hospital);
            if (tipo.equals("URGENTES")) {
                valores.put("titulo", "Turnos urgentes");
            } else if (tipo.equals("PREMIUM")) {
                valores.put("titulo", "Turnos de obras sociales premium");
            } else {
                valores.put("titulo", "Todos los turnos");
            }
            StringSubstitutor sustitutor = StringSubstitutor.createInterpolator();
            sustitutor.setVariableResolver(
                    clave ->
                            valores.containsKey(clave)
                                    ? valores.get(clave)
                                    : StringSubstitutor.createInterpolator()
                                            .replace("${" + clave + "}"));
            String encabezado = sustitutor.replace("${hospital} - ${titulo}");
            if (formato.equals("TXT")) {
                salida = encabezado + "\n";
                for (String t : turnos) {
                    String[] p = t.split("-");
                    if (tipo.equals("URGENTES")) {
                        if (p.length == 4 && p[3].equals("URGENTE")) {
                            salida = salida + p[0] + " (" + p[1] + " anios) - " + p[2] + "\n";
                        }
                    } else if (tipo.equals("PREMIUM")) {
                        if (p[2].equals("OSDE") || p[2].equals("SWISS")) {
                            salida = salida + p[0] + " (" + p[1] + " anios) - " + p[2] + "\n";
                        }
                    } else {
                        salida = salida + p[0] + " (" + p[1] + " anios) - " + p[2] + "\n";
                    }
                }
            } else if (formato.equals("CSV")) {
                salida = "paciente;edad;obra_social\n";
                for (String t : turnos) {
                    String[] p = t.split("-");
                    if (tipo.equals("URGENTES")) {
                        if (p.length == 4 && p[3].equals("URGENTE")) {
                            salida = salida + p[0] + ";" + p[1] + ";" + p[2] + "\n";
                        }
                    } else if (tipo.equals("PREMIUM")) {
                        if (p[2].equals("OSDE") || p[2].equals("SWISS")) {
                            salida = salida + p[0] + ";" + p[1] + ";" + p[2] + "\n";
                        }
                    } else {
                        salida = salida + p[0] + ";" + p[1] + ";" + p[2] + "\n";
                    }
                }
            } else if (formato.equals("HTML")) {
                salida = "<h1>" + encabezado + "</h1>\n<ul>\n";
                for (String t : turnos) {
                    String[] p = t.split("-");
                    if (tipo.equals("URGENTES")) {
                        if (p.length == 4 && p[3].equals("URGENTE")) {
                            salida =
                                    salida
                                            + "<li>"
                                            + p[0]
                                            + " ("
                                            + p[1]
                                            + " anios) - "
                                            + p[2]
                                            + "</li>\n";
                        }
                    } else if (tipo.equals("PREMIUM")) {
                        if (p[2].equals("OSDE") || p[2].equals("SWISS")) {
                            salida =
                                    salida
                                            + "<li>"
                                            + p[0]
                                            + " ("
                                            + p[1]
                                            + " anios) - "
                                            + p[2]
                                            + "</li>\n";
                        }
                    } else {
                        salida =
                                salida
                                        + "<li>"
                                        + p[0]
                                        + " ("
                                        + p[1]
                                        + " anios) - "
                                        + p[2]
                                        + "</li>\n";
                    }
                }
                salida = salida + "</ul>\n";
            }
        }
        return salida;
    }
}
