# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia tudo para dentro do container
COPY . .

# Comando "mágico" para entrar na pasta certa se o seu projeto não estiver na raiz
# Ele procura o pom.xml e entra na pasta dele antes de rodar o Maven
RUN find . -name "pom.xml" -exec dirname {} \; > project_dir.txt && \
    cd $(cat project_dir.txt) && \
    mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre
WORKDIR /app

# Busca o arquivo .jar gerado em qualquer subpasta de target
COPY --from=build /app/**/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]