#!/bin/bash
# 1. Tenta encerrar o processo do Java que está rodando o seu JAR
# O '|| true' garante que o script não dê erro se o app já estiver parado
pkill -f 'stock-manager.jar' || true

# 2. Pequena pausa para garantir que a porta 8080 seja liberada pelo SO
sleep 5