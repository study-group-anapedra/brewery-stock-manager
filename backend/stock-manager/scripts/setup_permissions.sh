#!/bin/bash
set -e

# Adicione esta definição para segurança
APP_DIR="/home/ec2-user/app"

echo "Configurando permissões da aplicação em $APP_DIR..."

mkdir -p $APP_DIR
rm -f $APP_DIR/*.log || true

chown -R ec2-user:ec2-user $APP_DIR
chmod -R 755 $APP_DIR

# Garante que os scripts baixados pelo CodeDeploy possam ser executados nos próximos hooks
chmod -R +x $APP_DIR/scripts/
chmod 755 $APP_DIR/stock-manager.jar

echo "Permissões configuradas com sucesso."