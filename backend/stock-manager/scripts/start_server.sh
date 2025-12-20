#!/bin/bash
# Entra na pasta onde o arquivo foi instalado
cd /home/ec2-user/app
# Inicia o Java em segundo plano com o perfil de produção que você configurou
nohup java -jar -Dspring.profiles.active=prod stock-manager.jar > /dev/null 2>&1 &