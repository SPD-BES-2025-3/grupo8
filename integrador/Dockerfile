# Usa uma imagem base do Java 17
FROM eclipse-temurin:17-jdk-jammy

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo .jar compilado do seu projeto para o container
COPY target/integrador-camel-jms-1.0-SNAPSHOT.jar app.jar

# Comando para executar a aplicação, incluindo o '--add-opens'
ENTRYPOINT ["java", "--add-opens", "java.base/java.time=ALL-UNNAMED", "-jar", "app.jar"]