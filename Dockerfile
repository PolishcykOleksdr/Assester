FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp package -DskipTests \
    && find /build/target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' \
       -exec cp {} /build/app.jar \;

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S -G app app
RUN mkdir -p /app/logs/info /app/logs/error /app/logs/debug && \
    chown -R app:app /app
COPY --from=build --chown=app:app /build/app.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
