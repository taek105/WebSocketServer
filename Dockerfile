# 1단계 : 빌드
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

COPY . .

RUN ./gradlew --no-daemon clean bootJar

#2단계 : 런타임
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8000
ENTRYPOINT ["java","-jar","app.jar"]
