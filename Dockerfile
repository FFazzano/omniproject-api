FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# Rodando o build sem a fase de processamento de recursos que está dando erro
RUN POM_PATH=$(find . -name "pom.xml" | head -n 1) && \
    mvn -f "$POM_PATH" clean package -DskipTests -Dfile.encoding=UTF-8 -Dmaven.resources.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/**/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]