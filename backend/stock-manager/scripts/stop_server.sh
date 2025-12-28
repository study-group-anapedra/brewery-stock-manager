#!/bin/bash
set +e

APP_NAME="stock-manager.jar"

echo "Parando aplicação se estiver rodando..."

# Encerra qualquer processo Java rodando o JAR
pkill -f "$APP_NAME" || true

# Aguarda liberação de recursos (porta, memória)
sleep 5

echo "Aplicação parada (ou já estava parada)."
