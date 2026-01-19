#!/bin/bash
set -e

# CORREÇÃO: Caminho alinhado com o UserData da Infra
APP_DIR="/home/ec2-user/app"

echo "Configurando permissões da aplicação..."

# Garante que o diretório existe
mkdir -p $APP_DIR

# CORREÇÃO: Garantindo que o ec2-user tenha acesso, mas o CodeDeploy (root) possa operar
chown -R ec2-user:ec2-user $APP_DIR
chmod -R 755 $APP_DIR

echo "Permissões configuradas com sucesso."