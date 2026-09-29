# Funciones comunes de los scripts. Se incluye con: source scripts/lib.sh
set -euo pipefail
RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LIB="$RAIZ/lib"
MAVEN=https://repo1.maven.org/maven2
mkdir -p "$LIB/deps" "$LIB/herramientas"

bajar() {  # url destino
    [ -f "$2" ] || curl -fsSL "$1" -o "$2"
}

# Baja cada dependencia declarada en pom.xml y deja el classpath en CP_MAIN y CP_TEST.
resolver_dependencias() {
    CP_MAIN=""
    CP_TEST=""
    while IFS=' ' read -r grupo artefacto version alcance; do
        local jar="$LIB/deps/$artefacto-$version.jar"
        bajar "$MAVEN/${grupo//.//}/$artefacto/$version/$artefacto-$version.jar" "$jar"
        if [ "$alcance" = "test" ]; then
            CP_TEST="$CP_TEST:$jar"
        else
            CP_MAIN="$CP_MAIN:$jar"
        fi
    done < <(python3 - "$RAIZ/pom.xml" <<'PY'
import sys, xml.etree.ElementTree as ET
ns = {"m": "http://maven.apache.org/POM/4.0.0"}
for d in ET.parse(sys.argv[1]).getroot().findall("m:dependencies/m:dependency", ns):
    t = lambda k, dflt=None: d.findtext("m:" + k, default=dflt, namespaces=ns)
    print(t("groupId"), t("artifactId"), t("version"), t("scope", "compile"))
PY
)
    CP_MAIN="${CP_MAIN#:}"
    CP_TEST="$CP_MAIN$CP_TEST"
}

GJF="$LIB/herramientas/google-java-format-1.23.0-all-deps.jar"
CHECKSTYLE="$LIB/herramientas/checkstyle-10.18.1-all.jar"
JACOCO_AGENTE="$LIB/herramientas/org.jacoco.agent-0.8.12-runtime.jar"
JACOCO_CLI="$LIB/herramientas/org.jacoco.cli-0.8.12-nodeps.jar"

bajar_herramientas() {
    bajar https://github.com/google/google-java-format/releases/download/v1.23.0/google-java-format-1.23.0-all-deps.jar "$GJF"
    bajar https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.18.1/checkstyle-10.18.1-all.jar "$CHECKSTYLE"
    bajar "$MAVEN/org/jacoco/org.jacoco.agent/0.8.12/org.jacoco.agent-0.8.12-runtime.jar" "$JACOCO_AGENTE"
    bajar "$MAVEN/org/jacoco/org.jacoco.cli/0.8.12/org.jacoco.cli-0.8.12-nodeps.jar" "$JACOCO_CLI"
}

# google-java-format necesita acceso a los internos de javac en Java 17+.
formateador() {
    java --add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
         --add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
         --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
         --add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
         --add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
         --add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
         -jar "$GJF" --aosp "$@"
}
