#!/bin/bash
set -e

# 1. Navega para a pasta onde o CodeDeploy colocou os arquivos
cd /home/ec2-user/app

echo "Buscando endpoint do banco via CloudFormation..."

# 2. Busca o endpoint do RDS a partir do stack de infra
DB_ENDPOINT=$(aws cloudformation describe-stacks \
  --stack-name stock-manager-master-dev \
  --query "Stacks[0].Outputs[?OutputKey=='DBEndpoint'].OutputValue" \
  --output text)

if [ -z "$DB_ENDPOINT" ]; then
  echo "Erro: DBEndpoint não encontrado"
  exit 1
fi

# 3. Exporta as variáveis esperadas pelo application-prod.yml
export DB_HOST="$DB_ENDPOINT"
export DB_NAME="stockmanager"          # ajuste se necessário
export DB_USERNAME="admin"
export DB_PASSWORD="StockManagerProd2025"

echo "Iniciando Stock Manager em modo Produção..."
echo "Conectando no banco em: $DB_HOST"

# 4. Executa o JAR
nohup java -Dspring.profiles.active=prod \
           -jar stock-manager.jar > log.txt 2>&1 &

echo "Aplicação disparada em segundo plano."
