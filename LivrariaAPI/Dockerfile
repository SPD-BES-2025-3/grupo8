# Usa uma imagem base do Java 17
FROM eclipse-temurin:17-jdk-jammy

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo .jar compilado do seu projeto para o container
COPY target/api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que a API usa
EXPOSE 8080

# Comando para executar a aplicação quando o container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]