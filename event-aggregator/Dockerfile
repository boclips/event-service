FROM openjdk:8-jre-slim
COPY build/libs/*.jar /opt/
WORKDIR /opt
CMD ["java", "-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/dumps/oom.hprof", "-cp", "/opt/*", "com.boclips.event.aggregator.EventAggregatorApp"]
