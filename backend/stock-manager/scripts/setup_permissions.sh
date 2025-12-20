#!/bin/bash
# Garante que o usuário ec2-user seja o dono da pasta do app na AWS
sudo chown -R ec2-user:ec2-user /home/ec2-user/app
# Dá permissão para os scripts serem executados
sudo chmod +x /home/ec2-user/app/scripts/*.sh