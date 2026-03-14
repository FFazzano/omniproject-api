# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# Esse comando acha o pom.xml sozinho e roda o build lá dentro
RUN POM_PATH=$(find . -name "pom.xml" | head -n 1) && \
    mvn -f "$POM_PATH" clean package -DskipTests -Dfile.encoding=UTF-8

# Estágio de Execução
FROM eclipse-temurin:21-jre
WORKDIR /app

# Busca o JAR em qualquer pasta target que tenha sido criada
COPY --from=build /app/**/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]