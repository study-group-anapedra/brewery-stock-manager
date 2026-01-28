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
# Garante que o ec2-user consiga gerenciar os arquivos
chown -R ec2-user:ec2-user "$APP_DIR"

# 2. Carregamento de credenciais
if [ ! -f "$ENV_FILE" ]; then
  echo "ERRO CRÍTICO: Arquivo de ambiente $ENV_FILE não encontrado."
  exit 1
fi

echo "Carregando segredos de infraestrutura..."
set -a
source "$ENV_FILE"
set +a

# 3. Validação de Rede (Selo de Sanidade)
echo "Validando conexão TCP com o RDS em $DB_HOST:$DB_PORT..."
if ! nc -zv "$DB_HOST" "$DB_PORT"; then
  echo "ERRO: Banco de dados inacessível via rede. Verifique o Security Group."
  exit 1
fi

# 4. Configuração explícita para o Spring Boot
export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="${DB_USERNAME}"
export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"

# 4.5 Validação do artefato (JAR)
if [ ! -f "$APP_DIR/$APP_NAME" ]; then
  echo "ERRO CRÍTICO: JAR $APP_NAME não encontrado em $APP_DIR"
  exit 1
fi

# 5. Inicialização do Processo
cd "$APP_DIR"
echo "Subindo JVM com perfil de produção..."

# Executa como ec2-user para evitar rodar a aplicação como root
sudo -u ec2-user nohup java -Dspring.profiles.active=prod -jar "$APP_NAME" > "$LOG_FILE" 2>&1 &

# Aguarda a JVM iniciar e tentar conexão com o banco
sleep 30

# 6. Verificação de Saúde
if pgrep -f "$APP_NAME" > /dev/null; then
  echo "SUCESSO: Aplicação Java está rodando."
  echo "PID(s): $(pgrep -f "$APP_NAME")"
  exit 0
else
  echo "FALHA: Aplicação morreu após o startup. Verificando logs internos:"
  tail -n 100 "$LOG_FILE"
  exit 1
fi
