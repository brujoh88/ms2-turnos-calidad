# Definition of Done — Módulo de Turnos, Hospital Central

Se aplica a **todo** incremento que se quiera integrar a `main`. Una tarea está
*Terminada* solo si cumple los seis criterios; si falla uno, el Pull Request no se mezcla.

| # | Criterio | Qué se exige | Cómo se verifica en este repo |
|:---:|---|---|---|
| 1 | Integridad de compilación | Cero errores y cero warnings | `javac -Xlint:all -Werror` dentro de `scripts/build.sh` |
| 2 | Pruebas automatizadas | 100 % de la suite en verde | JUnit 5 en `scripts/build.sh`, corrido por el pre-commit hook |
| 3 | Cobertura mínima | ≥ 80 % de las líneas nuevas o modificadas | Reporte JaCoCo en `build/reporte/` |
| 4 | Revisión por pares | Aprobación de al menos un integrante distinto del autor | Revisión del Pull Request |
| 5 | Gobernanza de estilo | Sin violaciones de formato ni de reglas | google-java-format + Checkstyle en el pre-commit hook |
| 6 | Documentación técnica | Si cambia un contrato, se documenta en el mismo PR | Checklist de la revisión |

Solo se mezcla lo que tiene la etiqueta `dod:done`.
