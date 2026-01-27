#!/bin/bash
set -e
APP_NAME="stock-manager.jar"

echo "Parando aplicação..."
pkill -15 -f "$APP_NAME" || true
sleep 5
pkill -9 -f "$APP_NAME" || true
echo "Aplicação parada."
