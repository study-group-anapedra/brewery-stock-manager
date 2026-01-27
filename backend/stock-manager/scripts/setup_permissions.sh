#!/bin/bash
set -e

echo "Carregando variáveis globais do sistema..."

# Carrega variáveis de ambiente do sistema
if [ -f /etc/environment ]; then
  set -a
  source /etc/environment
  set +a
fi

# Carrega qualquer profile existente
if [ -f /etc/profile ]; then
  source /etc/profile
fi

if [ -d /etc/profile.d ]; then
  for f in /etc/profile.d/*.sh; do
    [ -r "$f" ] && source "$f"
  done
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

echo "BeforeInstall concluído com sucesso."

