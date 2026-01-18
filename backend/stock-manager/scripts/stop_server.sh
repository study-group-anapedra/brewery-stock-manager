#!/bin/bash
set -e

APP_NAME="app.jar"

echo "Parando aplicação..."

# Mata qualquer processo Java rodando com o app.jar
pkill -f "$APP_NAME" || true

sleep 5

echo "Aplicação parada (ou já estava parada)."
