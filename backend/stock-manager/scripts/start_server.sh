#!/bin/bash
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="/home/ec2-user/app/stock-manager.log"

# 1. Carrega variáveis injetadas pela infra (Opcional)
if [ -f /etc/profile.d/app_env.sh ]; then
    source /etc/profile.d/app_env.sh
fi

# 2. Credenciais e Host (Sincronizados com seu RDS v2) [cite: 2025-12-23]
export DB_HOST="stock-manager-core-prod-rdsstack-ph3lf1-dbinstance-hsbntw2eom7c.cafmokswu8mk.us-east-1.rds.amazonaws.com"
export DB_PORT="5432"
export DB_NAME="stockmanagerprod"
export DB_USERNAME="dbadmin"
export DB_PASSWORD="StockManagerProd2025"

# 3. Monta a URL JDBC para o Spring Boot [cite: 2025-12-21]
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

mkdir -p $APP_DIR
cd $APP_DIR

echo "Iniciando aplicação com Banco: $DB_HOST"

# 4. Execução em background
nohup java -Dspring.profiles.active=prod -jar $APP_NAME > $LOG_FILE 2>&1 &

# Pausa para o Java tentar subir
sleep 25

# 5. Validação de inicialização
if pgrep -f "$APP_NAME" > /dev/null
then
    echo "Sucesso: Processo Java ativo."
else
    echo "Erro: Aplicação falhou ao conectar no banco $DB_HOST"
    tail -n 50 $LOG_FILE
    exit 1
fi