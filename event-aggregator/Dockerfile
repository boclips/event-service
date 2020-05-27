FROM openjdk:8-jre-slim
COPY build/libs/*.jar /opt/
WORKDIR /opt
CMD ["java", "-cp", "/opt/*", "com.boclips.event.aggregator.EventAggregatorApp"]
