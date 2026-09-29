#!/usr/bin/env python3
"""Escanea las dependencias declaradas en pom.xml contra la base publica OSV
(que agrupa los avisos de GitHub y los CVE). Sale con codigo 1 si encuentra
alguna vulnerabilidad, para que pueda cortar un hook o un pipeline."""
import json
import sys
import urllib.request
import xml.etree.ElementTree as ET

NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def dependencias(pom):
    raiz = ET.parse(pom).getroot()
    for dep in raiz.findall("m:dependencies/m:dependency", NS):
        grupo = dep.findtext("m:groupId", namespaces=NS)
        artefacto = dep.findtext("m:artifactId", namespaces=NS)
        version = dep.findtext("m:version", namespaces=NS)
        alcance = dep.findtext("m:scope", default="compile", namespaces=NS)
        yield f"{grupo}:{artefacto}", version, alcance


def consultar(paquete, version):
    cuerpo = json.dumps({"version": version,
                         "package": {"name": paquete, "ecosystem": "Maven"}}).encode()
    pedido = urllib.request.Request("https://api.osv.dev/v1/query", data=cuerpo,
                                    headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(pedido, timeout=30) as respuesta:
        return json.load(respuesta).get("vulns", [])


def severidad(vuln):
    return (vuln.get("database_specific") or {}).get("severity", "SIN DATO")


def main():
    pom = sys.argv[1] if len(sys.argv) > 1 else "pom.xml"
    total = 0
    print(f"Escaneo de dependencias de {pom} contra OSV (osv.dev)\n")
    for paquete, version, alcance in dependencias(pom):
        vulns = consultar(paquete, version)
        estado = "OK" if not vulns else f"{len(vulns)} VULNERABILIDAD(ES)"
        print(f"  {paquete}:{version} [{alcance}] -> {estado}")
        for v in vulns:
            cves = ", ".join(a for a in v.get("aliases", []) if a.startswith("CVE")) or "-"
            print(f"      {v['id']} ({cves}) severidad {severidad(v)}: {v.get('summary', '')}")
        total += len(vulns)
    print(f"\nTotal de vulnerabilidades conocidas: {total}")
    sys.exit(1 if total else 0)


if __name__ == "__main__":
    main()
