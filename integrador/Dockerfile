FROM eclipse-temurin:17-jdk-jammy

# Define o diretório onde o CÓDIGO da aplicação ficará
WORKDIR /app

# Copia o arquivo .jar compilado para o diretório de código
COPY target/integrador-camel-jms-1.0-SNAPSHOT.jar app.jar

# Define o diretório onde os DADOS serão gravados
VOLUME /data
WORKDIR /data

# Comando para executar a aplicação a partir de seu local fixo
ENTRYPOINT ["java", "--add-opens", "java.base/java.time=ALL-UNNAMED", "-jar", "/app/app.jar"]