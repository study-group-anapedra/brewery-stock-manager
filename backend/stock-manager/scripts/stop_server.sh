#!/bin/bash
set -e


APP_NAME="stock-manager.jar"

echo "Parando aplicação..."

# O "|| true" evita que o script falhe caso a aplicação não esteja rodando
pkill -f "$APP_NAME" || true

sleep 5

echo "Aplicação parada (ou já estava parada)."
