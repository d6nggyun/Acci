FROM eclipse-temurin:17-jre-alpine
ARG JAR_NAME=Acci-0.0.1-SNAPSHOT.jar
COPY ./build/libs/${JAR_NAME} app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]