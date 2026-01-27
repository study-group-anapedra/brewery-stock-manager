#!/bin/bash
set -e

# Carrega variáveis globais do sistema (CodeDeploy não herda shell interativo)
if [ -f /etc/environment ]; then
  export $(grep -v '^#' /etc/environment | xargs)
fi

APP_DIR="/home/ec2-user/app"
ENV_FILE="/etc/profile.d/app_env.sh"
ENV_DIR="/etc/profile.d"

echo "Configurando diretórios base da aplicação em $APP_DIR..."

mkdir -p "$APP_DIR"
rm -f "$APP_DIR"/*.log || true

chown -R ec2-user:ec2-user "$APP_DIR"
chmod -R 755 "$APP_DIR"

echo "Validando variáveis de banco antes de criar $ENV_FILE..."

: "${DB_HOST:?DB_HOST não definido no ambiente da instância}"
: "${DB_PORT:?DB_PORT não definido no ambiente da instância}"
: "${DB_NAME:?DB_NAME não definido no ambiente da instância}"
: "${DB_USERNAME:?DB_USERNAME não definido no ambiente da instância}"
: "${DB_PASSWORD:?DB_PASSWORD não definido no ambiente da instância}"

echo "Criando arquivo de variáveis de ambiente do banco em $ENV_FILE..."

mkdir -p "$ENV_DIR"

cat <<EOF > "$ENV_FILE"
export DB_HOST="${DB_HOST}"
export DB_PORT="${DB_PORT}"
export DB_NAME="${DB_NAME}"
export DB_USERNAME="${DB_USERNAME}"
export DB_PASSWORD="${DB_PASSWORD}"
EOF

chmod 600 "$ENV_FILE"
chown root:root "$ENV_FILE"

echo "Ajustando permissões da aplicação (se os arquivos já existirem)..."

# Só ajusta permissões se os arquivos já existirem
if [ -d "$APP_DIR/scripts" ]; then
  find "$APP_DIR/scripts" -type f -name "*.sh" -exec chmod +x {} \; || true
fi

if [ -f "$APP_DIR/stock-manager.jar" ]; then
  chmod 755 "$APP_DIR/stock-manager.jar"
fi

echo "Permissões e variáveis de ambiente configuradas com sucesso."
