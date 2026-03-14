# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia tudo do repositório para o container
COPY . .

# ENTRA NA PASTA API (onde está o seu pom.xml) E FAZ O BUILD
# Se a pasta não se chamar API, me avise!
RUN cd API && mvn clean package -DskipTests -Dfile.encoding=UTF-8

# Estágio de Execução
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o JAR da pasta target que está dentro de API
COPY --from=build /app/API/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]