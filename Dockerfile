# 可能需要先 docker pull openjdk:21
# 第一阶段：构建 Jar
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：运行应用
FROM openjdk:21
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
