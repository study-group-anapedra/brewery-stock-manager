#!/bin/bash
# Interrompe o script em caso de qualquer erro
set -e

APP_NAME="stock-manager.jar"
APP_DIR="/home/ec2-user/app"
LOG_FILE="$APP_DIR/stock-manager.log"
ENV_FILE="/etc/profile.d/app_env.sh"

echo "=== [$(date)] Iniciando start da aplicação ==="
echo "Executando como usuário: $(whoami)"

# 1. Preparação do ambiente
mkdir -p "$APP_DIR"
touch "$LOG_FILE"
# Garante que o ec2-user consiga gerenciar os arquivos [cite: 2025-12-23]
chown -R ec2-user:ec2-user "$APP_DIR"

# 2. Carregamento de credenciais [cite: 2025-12-23]
if [ ! -f "$ENV_FILE" ]; then
  echo "ERRO CRÍTICO: Arquivo de ambiente $ENV_FILE não encontrado."
  exit 1
fi

echo "Carregando segredos de infraestrutura..."
set -a
source "$ENV_FILE"
set +a

# 3. Validação de Rede (O "Selo de Sanidade") [cite: 2025-12-23]
echo "Validando conexão TCP com o RDS em $DB_HOST:5432..."
if ! nc -zv "$DB_HOST" 5432; then
  echo "ERRO: Banco de dados inacessível via rede. Verifique o sg.yaml."
  exit 1
fi

# 4. Configuração explícita para o Spring Boot [cite: 2025-12-23]
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

# 5. Inicialização do Processo
cd "$APP_DIR"
echo "Subindo JVM com perfil de produção..."

# Executa como ec2-user para evitar rodar a aplicação como root por segurança
sudo -u ec2-user nohup java -Dspring.profiles.active=prod -jar "$APP_NAME" > "$LOG_FILE" 2>&1 &

# Aguarda a JVM alocar memória e iniciar o handshake com o banco [cite: 2025-12-23]
sleep 30

# 6. Verificação de Saúde
if pgrep -f "$APP_NAME" > /dev/null; then
  echo "SUCESSO: Aplicação Java está rodando."
  exit 0
else
  echo "FALHA: Aplicação morreu após o startup. Verificando logs internos:"
  tail -n 100 "$LOG_FILE"
  exit 1
fi

