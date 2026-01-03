# ---------- 1) Build Stage ----------
FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /workspace

# Gradle 캐시 최적화: 설정/의존 스텝 먼저 복사
COPY env.properties .
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew .
COPY gradlew.bat .
COPY gradle ./gradle

RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon dependencies || true

# 이후 전체 복사 → 소스 변경 시 여기서부터만 캐시 무효화
COPY src ./src

# 테스트는 이미지 빌드 속도를 위해 보통 스킵(필요시 제거)
RUN ./gradlew clean bootJar -x test --no-daemon

# 결과 JAR 경로 탐색(일반적으로 build/libs/*.jar)
# 멀티모듈이면 경로 조정 필요
RUN ls -al build/libs && \
    cp build/libs/*-SNAPSHOT.jar app.jar || \
    cp build/libs/*.jar app.jar

# ---------- 2) Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# 보안: 비루트 사용자
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 필요 포트
EXPOSE 8080

# 런타임 JVM 튜닝(기본값, 환경에 맞게 조정)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25"
# 스프링 프로필
ENV SPRING_PROFILES_ACTIVE=dev

# JAR 복사
COPY --from=builder /workspace/app.jar /app/app.jar

# 헬스체크(애플리케이션에 /actuator/health 가 있을 때 권장)
# 없으면 제거하거나 엔드포인트 맞춰 변경
#HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
#  CMD wget -qO- http://localhost:8080/actuator/health | grep '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]