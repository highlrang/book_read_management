# ---------- 1) Build Stage ----------
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /workspace

# Gradle 캐시 최적화
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew .
COPY gradle ./gradle

RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon dependencies || true

COPY src ./src

# 빌드 실행
RUN ./gradlew clean bootJar -x test --no-daemon

# JAR 파일 추출 (패턴 매칭 강화)
RUN cp build/libs/*.jar app.jar

# ---------- 2) Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# 타임존 설정 (Asia/Seoul)
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone

# 보안 설정
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25"
ENV SPRING_PROFILES_ACTIVE=dev

# 빌드 단계에서 생성된 jar 복사
COPY --from=builder /workspace/app.jar /app/app.jar

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]