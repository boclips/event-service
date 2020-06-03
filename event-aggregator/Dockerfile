FROM openjdk:10-jre-slim
COPY build/libs/*.jar /opt/
WORKDIR /opt
CMD ["java", "-Xms11g", "-Xmx11g", "-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/dumps/oom.hprof", "-cp", "/opt/*", "com.boclips.event.aggregator.EventAggregatorApp"]
