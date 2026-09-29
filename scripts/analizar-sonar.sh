#!/usr/bin/env bash
# Analisis estatico con SonarQube local (Docker).
# Requiere el servidor corriendo en localhost:9000 y un token en SONAR_TOKEN:
#   docker run -d --name sonarqube-ms2 -p 9000:9000 sonarqube:community
#   SONAR_TOKEN=... scripts/analizar-sonar.sh
source "$(dirname "$0")/lib.sh"
cd "$RAIZ"
: "${SONAR_TOKEN:?Falta la variable SONAR_TOKEN}"
scripts/build.sh > /dev/null
docker run --rm --network host \
    -e SONAR_HOST_URL=http://localhost:9000 -e SONAR_TOKEN \
    -v "$RAIZ:/usr/src" sonarsource/sonar-scanner-cli:latest \
    -Dsonar.projectVersion="$(git rev-parse --short HEAD)" \
    -Dsonar.scm.revision="$(git rev-parse HEAD)"
