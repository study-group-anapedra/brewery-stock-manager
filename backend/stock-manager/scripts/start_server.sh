#!/bin/bash
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="$APP_DIR/stock-manager.log"
ENV_FILE="/etc/profile.d/app_env.sh"

echo "Iniciando start da aplicação..."
echo "Executando como usuário: $(whoami)"

# Garante que o diretório existe
mkdir -p "$APP_DIR"

# Garante permissão de escrita no log
touch "$LOG_FILE"
chown -R ec2-user:ec2-user "$APP_DIR"

# Verifica se o arquivo de variáveis existe
if [ ! -f "$ENV_FILE" ]; then
  echo "ERRO: Arquivo de variáveis $ENV_FILE não encontrado."
  exit 1
fi

echo "Carregando variáveis de ambiente..."
set -a
source "$ENV_FILE"
set +a

echo "Variáveis carregadas:"
env | grep DB_

# Validação forte
: "${DB_HOST:?DB_HOST não definido}"
: "${DB_PORT:?DB_PORT não definido}"
: "${DB_NAME:?DB_NAME não definido}"
: "${DB_USERNAME:?DB_USERNAME não definido}"
: "${DB_PASSWORD:?DB_PASSWORD não definido}"

# Garante Java
if ! command -v java >/dev/null 2>&1; then
  echo "ERRO: Java não encontrado"
  exit 1
fi

java -version

# Garante que o JAR existe
if [ ! -f "$APP_DIR/$APP_NAME" ]; then
  echo "ERRO: $APP_NAME não encontrado em $APP_DIR"
  ls -l "$APP_DIR"
  exit 1
fi

# Variáveis Spring explícitas
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

echo "Spring datasource:"
echo "$SPRING_DATASOURCE_URL"

cd "$APP_DIR"

echo "Subindo aplicação..."

nohup java -Dspring.profiles.active=prod -jar "$APP_NAME" > "$LOG_FILE" 2>&1 &

sleep 20

if pgrep -f "$APP_NAME" > /dev/null; then
  echo "Aplicação iniciada com sucesso."
  exit 0
else
  echo "Aplicação não subiu. Logs:"
  tail -n 200 "$LOG_FILE"
  exit 1
fi

