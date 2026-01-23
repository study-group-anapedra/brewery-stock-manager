#!/bin/bash
set -e

APP_DIR="/home/ec2-user/app"

echo "Configurando permissões da aplicação em $APP_DIR..."

# 1. Garante que o diretório existe
mkdir -p $APP_DIR

# 2. (Opcional) Limpa logs antigos para começar do zero
rm -f $APP_DIR/*.log || true

# 3. Garante que o ec2-user seja o dono de tudo dentro da pasta
chown -R ec2-user:ec2-user $APP_DIR
chmod -R 755 $APP_DIR

echo "Permissões configuradas com sucesso."