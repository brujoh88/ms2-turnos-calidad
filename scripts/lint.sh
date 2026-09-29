#!/usr/bin/env bash
# Linter: Checkstyle con las reglas del proyecto (config/checkstyle.xml).
#   scripts/lint.sh [archivos...]   sin argumentos revisa todo src/
source "$(dirname "$0")/lib.sh"
cd "$RAIZ"
bajar_herramientas
ARCHIVOS=("$@")
[ ${#ARCHIVOS[@]} -gt 0 ] || mapfile -t ARCHIVOS < <(find src -name '*.java')
java -jar "$CHECKSTYLE" -c config/checkstyle.xml "${ARCHIVOS[@]}"
