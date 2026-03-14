# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Executa o build forçando o encoding UTF-8 para evitar o erro MalformedInput
RUN find . -name "pom.xml" -exec dirname {} \; > project_dir.txt && \
    cd $(cat project_dir.txt) && \
    mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Estágio de Execução
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o jar gerado (o comando find garante que pegamos o arquivo certo)
COPY --from=build /app/**/target/*.jar app.jar

# Define que o Java deve rodar em UTF-8 também
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]