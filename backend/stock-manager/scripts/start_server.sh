#!/bin/bash
cd /home/ec2-user/app

# 1. Buscar o Endpoint do Banco de Dados via CLI da AWS
# Isso evita que você tenha que digitar o endereço manualmente
DB_URL=$(aws cloudformation describe-stacks \
  --stack-name stock-manager-master-dev \
  --query "Stacks[0].Outputs[?OutputKey=='DBEndpoint'].OutputValue" \
  --output text)

# 2. As credenciais nós passamos via ambiente ou segredos do CodeDeploy
# Para este teste, vamos usar as que você definiu na infra de Prod
DB_USERNAME="admin"
DB_PASSWORD="StockManagerProd2025"

echo "Iniciando aplicação conectando em: $DB_URL"

# 3. Comando Java com as variáveis injetadas
nohup java -Dspring.profiles.active=prod \
           -DDB_URL=$DB_URL \
           -DDB_USERNAME=$DB_USERNAME \
           -DDB_PASSWORD=$DB_PASSWORD \
           -jar stock-manager.jar > log.txt 2>&1 &