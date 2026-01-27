#!/bin/bash
set -e

APP_DIR="/home/ec2-user/app"
ENV_FILE="/etc/profile.d/app_env.sh"

echo "Configurando diretórios e permissões da aplicação em $APP_DIR..."

mkdir -p "$APP_DIR"
rm -f "$APP_DIR"/*.log || true

chown -R ec2-user:ec2-user "$APP_DIR"
chmod -R 755 "$APP_DIR"

chmod +x "$APP_DIR/scripts/"*.sh
chmod 755 "$APP_DIR/stock-manager.jar"

echo "Validando variáveis de banco antes de criar $ENV_FILE..."

: "${DB_HOST:?DB_HOST não definido no ambiente da instância}"
: "${DB_NAME:?DB_NAME não definido no ambiente da instância}"
: "${DB_USERNAME:?DB_USERNAME não definido no ambiente da instância}"
: "${DB_PASSWORD:?DB_PASSWORD não definido no ambiente da instância}"

echo "Criando arquivo de variáveis de ambiente do banco em $ENV_FILE..."

cat <<EOF > "$ENV_FILE"
export DB_HOST=${DB_HOST}
export DB_PORT=5432
export DB_NAME=${DB_NAME}
export DB_USERNAME=${DB_USERNAME}
export DB_PASSWORD=${DB_PASSWORD}
EOF

chmod 600 "$ENV_FILE"
chown root:root "$ENV_FILE"

echo "Permissões e variáveis de ambiente configuradas com sucesso."

