#!/usr/bin/env bash
# Activa los hooks versionados en .githooks/ para este clon del repositorio.
cd "$(dirname "$0")/.."
git config core.hooksPath .githooks
chmod +x .githooks/*
echo "Hooks activados: $(git config core.hooksPath)"
