#!/bin/bash
set -e

APP_NAME="app.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="/home/ec2-user/stock-manager.log"

cd $APP_DIR

echo "Iniciando aplicação..."

nohup java -Dspring.profiles.active=prod -jar $APP_NAME > $LOG_FILE 2>&1 &

sleep 5

echo "Aplicação iniciada."
