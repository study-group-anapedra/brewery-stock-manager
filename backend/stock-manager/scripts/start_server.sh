#!/bin/bash
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="$APP_DIR/stock-manager.log"
ENV_FILE="/etc/profile.d/app_env.sh"

echo "Iniciando start da aplicação..."

# Verifica se o arquivo de variáveis existe
if [ ! -f "$ENV_FILE" ]; then
  echo "ERRO: Arquivo de variáveis $ENV_FILE não encontrado."
  echo "Deploy abortado por falta de configuração de ambiente."
  exit 1
fi

# Carrega variáveis de ambiente
source "$ENV_FILE"

# Validação forte das variáveis obrigatórias
: "${DB_HOST:?DB_HOST não definido}"
: "${DB_PORT:?DB_PORT não definido}"
: "${DB_NAME:?DB_NAME não definido}"
: "${DB_USERNAME:?DB_USERNAME não definido}"
: "${DB_PASSWORD:?DB_PASSWORD não definido}"

# Alinhamento com o Spring Boot
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

cd "$APP_DIR"

echo "Subindo aplicação conectando no banco: $DB_HOST"

nohup java -Dspring.profiles.active=prod -jar "$APP_NAME" > "$LOG_FILE" 2>&1 &

# Tempo para a JVM subir e tentar conexão com o banco
sleep 30

# Validação do processo
if pgrep -f "$APP_NAME" > /dev/null; then
  echo "Aplicação iniciada com sucesso."
else
  echo "Falha ao subir a aplicação. Últimos logs:"
  tail -n 100 "$LOG_FILE"
  exit 1
fi

