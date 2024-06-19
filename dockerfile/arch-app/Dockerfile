FROM openjdk:17-alpine

MAINTAINER reionchan

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    PORT="8080" \
    PROFILES="dev" \
    JAVA_OPTS="-Duser.timezone=GMT+08 -Dfile.encoding=UTF-8 -javaagent:/opentelemetry-javaagent.jar" \
    ISSUE_ADDR="http://arch-iam:9090" \
    OTEL_SERVICE_NAME="arch-app" \
    OTEL_EXPORTER_OTLP_ENDPOINT="http://arch-otel-col:4318" \
    OTEL_LOGS_EXPORTER="otlp"

ADD ./arch-business/arch-app/target/*.jar /app.jar
ADD ./dockerfile/agent/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

HEALTHCHECK --interval=15s --retries=5 --timeout=10s CMD wget -nv -t=1 -qO- http://localhost:$PORT/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar --server.port=$PORT --spring.profiles.active=$PROFILES"]

EXPOSE $PORT