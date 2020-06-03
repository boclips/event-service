FROM openjdk:8-jre-slim
COPY build/libs/*.jar /opt/
WORKDIR /opt
CMD ["java", "-Xms13g", "-Xmx13g", "-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/dumps/oom.hprof", "-cp", "/opt/*", "com.boclips.event.aggregator.EventAggregatorApp"]
