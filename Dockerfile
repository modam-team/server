# 빌드 스테이지
FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle* .
COPY build.gradle* .

RUN chmod +x ./gradlew

COPY . .

RUN ./gradlew --no-daemon clean bootJar -x test

# 런타임 스테이지
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
