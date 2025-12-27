#!/bin/bash
#!/bin/bash
set -e

APP_DIR=/home/ec2-user/app
LOG_FILE=$APP_DIR/log.txt

cd $APP_DIR

echo "Buscando endpoint do banco via CloudFormation..."

# Buscar o endpoint do RDS a partir do stack de infra
DB_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name stock-manager-master-dev \
  --query "Stacks[0].Outputs[?OutputKey=='DBEndpoint'].OutputValue" \
  --output text)

if [ -z "$DB_ENDPOINT" ]; then
  echo "Erro: DBEndpoint não encontrado no CloudFormation"
  exit 1
fi

# Variáveis esperadas pelo application-prod.yml
export DB_HOST="$DB_ENDPOINT"
export DB_NAME="stockmanager"        # ajuste se o nome do banco for outro
export DB_USERNAME="admin"
export DB_PASSWORD="StockManagerProd2025"

echo "Iniciando aplicação conectando em: $DB_HOST"

nohup java -Dspring.profiles.active=prod \
           -jar stock-manager.jar > "$LOG_FILE" 2>&1 &

echo "Aplicação iniciada com sucesso."
