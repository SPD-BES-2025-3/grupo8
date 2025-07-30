#!/bin/bash

echo "========================================="
echo "COMPILANDO TODOS OS MÓDULOS COM MAVEN..."
echo "========================================="
# Compila a API
mvn clean install -f LivrariaAPI/pom.xml

# Compila o Integrador
mvn clean package -f integrador/pom.xml

# Compila a Aplicação Desktop
mvn clean install -f biblioteca-desktop-app/pom.xml

# Verifica se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "ERRO: A compilação do Maven falhou. Abortando."
    exit 1
fi

echo "========================================="
echo "INICIANDO SERVIÇOS DE BACKEND COM DOCKER COMPOSE..."
echo "========================================="
# Usa o comando moderno "docker compose" (com espaço)
docker compose up --build -d

echo "========================================="
echo "Backend iniciado. Para ver os logs, use: docker compose logs -f"
echo "Para parar os serviços, use: docker compose down"
echo "========================================="
echo ""
echo "========================================="
echo "INICIANDO APLICAÇÃO DESKTOP (JAVA FX)..."
echo "========================================="
mvn javafx:run -f biblioteca-desktop-app/pom.xml

# Quando a aplicação desktop for fechada, para os containers do backend
echo "Aplicação Desktop fechada. Parando os serviços do Docker..."
# Usa o comando moderno "docker compose" (com espaço)
docker compose down
