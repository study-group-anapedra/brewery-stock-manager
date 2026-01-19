#!/bin/bash
set -e

# CORREÇÃO: Alinhado com o nome gerado no GitHub Actions
APP_NAME="stock-manager.jar" 
APP_DIR="/home/ec2-user/app"
LOG_FILE="/home/ec2-user/stock-manager.log"

cd $APP_DIR

echo "Iniciando aplicação..."

# CORREÇÃO: Usando o caminho absoluto do Java instalado na infra
nohup java -Dspring.profiles.active=prod -jar $APP_NAME > $LOG_FILE 2>&1 &

sleep 5
echo "Aplicação iniciada."
