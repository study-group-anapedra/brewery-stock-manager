#!/bin/bash
set -e

APP_DIR="/home/ec2-user/app"
LOG_FILE="$APP_DIR/app.log"
STACK_NAME="stock-manager-master-dev"

echo "Iniciando aplicação..."
cd $APP_DIR

echo "Buscando endpoint do banco via CloudFormation..."

DB_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name "$STACK_NAME" \
  --query "Stacks[0].Outputs[?OutputKey=='RDSHost'].OutputValue" \
  --output text)

if [ -z "$DB_ENDPOINT" ]; then
  echo "Erro: RDSHost não encontrado no CloudFormation"
  exit 1
fi

export DB_URL="$DB_ENDPOINT"
export DB_USERNAME="admin"
export DB_PASSWORD="StockManagerProd2025"

echo "Conectando no banco: $DB_URL"
echo "Iniciando Spring Boot em modo PROD..."

nohup java -Dspring.profiles.active=prod \
           -jar app.jar > "$LOG_FILE" 2>&1 &

echo "Aplicação iniciada com sucesso."
