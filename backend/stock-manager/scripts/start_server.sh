#!/bin/bash
# O comando 'set -e' faz o script parar se qualquer comando falhar
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="/home/ec2-user/stock-manager.log"

# 1. Carrega as variáveis que a infraestrutura injetou no Job 3 (Recomendado)
if [ -f /etc/profile.d/app_env.sh ]; then
    source /etc/profile.d/app_env.sh
fi

# 2. Define as variáveis de conexão (Valores que você forneceu)
export DB_HOST="stock-manager-prod-instance-1.cafmokswu8mk.us-east-1.rds.amazonaws.com"
export DB_PORT="5432"
export DB_NAME="stockmanagerprod"
export DB_USERNAME="dbadmin"
export DB_PASSWORD="StockManagerProd2025"

# 3. Monta a URL do JDBC para o Spring Boot (Essencial para o Java conectar)
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

cd $APP_DIR

echo "Iniciando aplicação Stock Manager no diretório $APP_DIR..."

# 4. Inicia o Java com o perfil de produção e as variáveis injetadas
# O 'nohup' garante que a app continue rodando após o CodeDeploy terminar
nohup java -Dspring.profiles.active=prod \
     -jar $APP_NAME > $LOG_FILE 2>&1 &

# 5. Pequena pausa para o Java tentar subir antes do script terminar
sleep 10

# Verifica se o processo Java realmente iniciou
if pgrep -f "$APP_NAME" > /dev/null
then
    echo "Aplicação iniciada com sucesso (PID: $(pgrep -f $APP_NAME))."
else
    echo "Erro: A aplicação falhou ao iniciar. Verifique os logs em $LOG_FILE"
    exit 1
fi
