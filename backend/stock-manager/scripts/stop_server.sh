#!/bin/bash
set -e

# CORREÇÃO: Nome alinhado com o JAR gerado no GitHub Actions
APP_NAME="stock-manager.jar"

echo "Parando aplicação..."

# CORREÇÃO: Mata o processo pelo nome correto do arquivo
# O "|| true" evita que o script falhe caso a aplicação não esteja rodando
pkill -f "$APP_NAME" || true

sleep 5

echo "Aplicação parada (ou já estava parada)."
