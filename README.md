# Módulo de turnos — Hospital Central

Proyecto de práctica de **Metodología de Sistemas II** (UTN - TUPaD), Unidad 3:
Verificación y Validación. Autor: Tiseira Gustavo Hernán.

Registro de turnos, cálculo del copago, cobro por una pasarela de pagos externa y
reporte de turnos. Java 21, sin Maven: las dependencias se declaran en `pom.xml`
y los scripts las bajan de Maven Central.

## Uso

```bash
scripts/instalar-hooks.sh          # activa el pre-commit hook (una vez por clon)
scripts/build.sh                   # compila, corre los tests y mide cobertura
scripts/formatear.sh [--check]     # google-java-format
scripts/lint.sh                    # Checkstyle
scripts/escanear_dependencias.py   # dependencias de pom.xml contra OSV (CVE)
```

## Calidad

- [Definition of Done](DOD.md)
- Plantillas de incidencias en `.github/ISSUE_TEMPLATE/`
- Pre-commit hook en `.githooks/pre-commit`: formatter → linter → pruebas
