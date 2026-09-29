#!/usr/bin/env bash
# Formatea el codigo con google-java-format (estilo AOSP, 4 espacios).
#   scripts/formatear.sh            reescribe todos los .java
#   scripts/formatear.sh --check    solo verifica; sale con 1 si algo no esta formateado
source "$(dirname "$0")/lib.sh"
cd "$RAIZ"
bajar_herramientas
if [ "${1:-}" = "--check" ]; then
    shift
    ARCHIVOS=("$@")
    [ ${#ARCHIVOS[@]} -gt 0 ] || mapfile -t ARCHIVOS < <(find src -name '*.java')
    formateador --dry-run --set-exit-if-changed "${ARCHIVOS[@]}"
else
    mapfile -t ARCHIVOS < <(find src -name '*.java')
    formateador --replace "${ARCHIVOS[@]}"
fi
