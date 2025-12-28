#!/bin/bash
set -e

APP_DIR="/home/ec2-user/app"
LOG_FILE="$APP_DIR/log.txt"
STACK_NAME="stock-manager-master-dev"

echo "Iniciando aplicação..."

cd $APP_DIR

echo "Buscando endpoint do banco via CloudFormation..."

DB_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name "$STACK_NAME" \
  --query "Stacks[0].Outputs[?OutputKey=='DBEndpoint'].OutputValue" \
  --output text)

if [ -z "$DB_ENDPOINT" ]; then
  echo "Erro: DBEndpoint não encontrado no CloudFormation"
  exit 1
fi

# Variáveis esperadas pelo Spring
export DB_HOST="$DB_ENDPOINT"
export DB_NAME="stockmanager"
export DB_USERNAME="admin"
export DB_PASSWORD="StockManagerProd2025"

echo "Conectando no banco: $DB_HOST"
echo "Iniciando Spring Boot em modo PROD..."

nohup java -Dspring.profiles.active=prod \
           -jar stock-manager.jar > "$LOG_FILE" 2>&1 &

echo "Aplicação iniciada com sucesso."

