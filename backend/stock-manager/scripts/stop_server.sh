#!/bin/bash
set -e

APP_NAME="stock-manager.jar"

echo "Iniciando processo de parada da aplicação $APP_NAME..."

# Tenta parar gentilmente primeiro
pkill -15 -f "$APP_NAME" || true
sleep 5

# Garante a parada forçada se ainda estiver rodando (evita porta ocupada)
pkill -9 -f "$APP_NAME" || true

echo "Aplicação parada com sucesso."