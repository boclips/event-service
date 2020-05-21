FROM openjdk:8-jre-slim
COPY event-service/build/libs/event-service-*.jar /opt/app.jar
WORKDIR /opt
CMD ["java", "-jar", "app.jar"]