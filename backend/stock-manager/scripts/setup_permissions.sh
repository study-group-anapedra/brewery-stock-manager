#!/bin/bash
set -e

APP_DIR="/home/ec2-user/app"

echo "Configurando permissões da aplicação..."

# Garante que o diretório existe
mkdir -p $APP_DIR

# Ajusta permissões para o usuário correto
chown -R ec2-user:ec2-user $APP_DIR
chmod -R 755 $APP_DIR

echo "Permissões configuradas com sucesso."
