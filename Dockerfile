# 可能需要先 docker pull openjdk:21
FROM openjdk:21

EXPOSE 8080

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
