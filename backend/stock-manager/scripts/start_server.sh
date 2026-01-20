#!/bin/bash
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="/home/ec2-user/stock-manager.log"

# ============================
# VARIÁVEIS DE BANCO
# ============================
export DB_HOST="stock-manager-prod-instance-1.cafmokswu8mk.us-east-1.rds.amazonaws.com"
export DB_PORT="5432"
export DB_NAME="stockmanagerprod"
export DB_USERNAME="dbadmin"
export DB_PASSWORD="StockManagerProd2025"

cd $APP_DIR

echo "Iniciando aplicação Stock Manager..."

nohup java -Dspring.profiles.active=prod -jar $APP_NAME > $LOG_FILE 2>&1 &

sleep 5

echo "Aplicação iniciada com sucesso."
