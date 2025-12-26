#!/bin/bash
# 1. Navega para a pasta onde o CodeDeploy colocou os arquivos
cd /home/ec2-user/app

# 2. Exporta as variáveis para o ambiente (O CodeDeploy deve injetar estas do GitHub)
# Se o DB_URL não vier da infra, você pode colocar o endpoint fixo aqui.
export DB_URL=${DB_URL}
export DB_USERNAME=${DB_USERNAME}
export DB_PASSWORD=${DB_PASSWORD}

echo "Iniciando Stock Manager em modo Produção..."

# 3. Executa o JAR conectando os Segredos do GitHub com o application-prod.properties
nohup java -Dspring.profiles.active=prod \
           -DDB_URL=$DB_URL \
           -DDB_USERNAME=$DB_USERNAME \
           -DDB_PASSWORD=$DB_PASSWORD \
           -jar stock-manager.jar > log.txt 2>&1 &

echo "Aplicação disparada em segundo plano."