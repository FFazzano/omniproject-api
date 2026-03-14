FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# O segredo: avisar ao Maven para ignorar erros de caracteres e usar UTF-8 no projeto todo
RUN cd API && mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8 -Dfile.encoding=UTF-8

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/API/target/*.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]