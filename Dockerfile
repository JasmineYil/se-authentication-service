FROM openjdk:21-slim-bullseye

COPY target/se-authentication-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app.jar"]