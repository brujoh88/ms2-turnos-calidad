#!/usr/bin/env bash
# Compila (warnings = error), corre la suite completa con JUnit 5 y mide cobertura con JaCoCo.
source "$(dirname "$0")/lib.sh"
cd "$RAIZ"
bajar_herramientas
resolver_dependencias

rm -rf build
mkdir -p build/main build/test build/reporte
javac -Xlint:all -Werror -cp "$CP_MAIN" -d build/main $(find src/main/java -name '*.java')
javac -Xlint:all,-processing -Werror -cp "build/main:$CP_TEST" -d build/test $(find src/test/java -name '*.java')

java -XX:+EnableDynamicAgentLoading -Xshare:off \
     -javaagent:"$JACOCO_AGENTE=destfile=build/jacoco.exec" \
     -jar "$LIB/deps/junit-platform-console-standalone-1.10.5.jar" execute \
     --class-path "build/main:build/test:$CP_TEST" \
     --scan-class-path build/test \
     --disable-banner --details=summary --details-theme=ascii \
     --reports-dir build/test-reports

java -jar "$JACOCO_CLI" report build/jacoco.exec --quiet \
     --classfiles build/main --sourcefiles src/main/java \
     --html build/reporte --csv build/reporte/cobertura.csv --xml build/reporte/cobertura.xml

python3 - <<'PY'
import csv
filas = list(csv.DictReader(open("build/reporte/cobertura.csv")))
def pct(c, m):
    return f"{100 * c / (c + m):5.1f} %" if c + m else "   -  "
lc = sum(int(f["LINE_COVERED"]) for f in filas); lm = sum(int(f["LINE_MISSED"]) for f in filas)
bc = sum(int(f["BRANCH_COVERED"]) for f in filas); bm = sum(int(f["BRANCH_MISSED"]) for f in filas)
print("\nCobertura (JaCoCo):")
for f in filas:
    c, m = int(f["LINE_COVERED"]), int(f["LINE_MISSED"])
    rc, rm = int(f["BRANCH_COVERED"]), int(f["BRANCH_MISSED"])
    print(f"  {f['CLASS']:22} lineas {pct(c, m)}   ramas {pct(rc, rm)}")
print(f"  {'TOTAL':22} lineas {pct(lc, lm)}   ramas {pct(bc, bm)}")
PY
