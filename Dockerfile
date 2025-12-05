# Build
FROM maven:3.9.0-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# JRE ultra minimal
FROM eclipse-temurin:17-jdk-alpine AS jre-builder
RUN apk add --no-cache upx && \
    jlink \
    --add-modules java.base,java.logging \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre && \
    find /jre -name "*.so" -exec upx --best {} \; || true

# Run desde scratch
FROM scratch
COPY --from=jre-builder /jre /jre
COPY --from=builder /app/target/*.jar /app.jar
USER 1000:1000
ENTRYPOINT ["/jre/bin/java", "-Xmx20m", "-jar", "/app.jar"]