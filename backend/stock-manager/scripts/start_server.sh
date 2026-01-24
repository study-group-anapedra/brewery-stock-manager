#!/bin/bash
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="/home/ec2-user/app/stock-manager.log"

# 1. Carrega variáveis exportadas pela infra (CloudFormation / CodeDeploy)
if [ -f /etc/profile.d/app_env.sh ]; then
  source /etc/profile.d/app_env.sh
fi

# 2. Validação mínima (para não subir quebrado)
: "${DB_HOST:?DB_HOST não definido}"
: "${DB_PORT:?DB_PORT não definido}"
: "${DB_NAME:?DB_NAME não definido}"
: "${DB_USERNAME:?DB_USERNAME não definido}"
: "${DB_PASSWORD:?DB_PASSWORD não definido}"

# 3. Monta a URL JDBC para o Spring Boot
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

mkdir -p "$APP_DIR"
cd "$APP_DIR"

echo "Iniciando aplicação com Banco: $DB_HOST"

# 4. Execução em background
nohup java -Dspring.profiles.active=prod -jar "$APP_NAME" > "$LOG_FILE" 2>&1 &

# Pausa para o Java tentar subir
sleep 25

# 5. Validação de inicialização
if pgrep -f "$APP_NAME" > /dev/null; then
  echo "Sucesso: Processo Java ativo."
else
  echo "Erro: Aplicação falhou ao conectar no banco $DB_HOST"
  tail -n 50 "$LOG_FILE"
  exit 1
fi
