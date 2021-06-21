FROM openjdk:10-jre-slim
ENV VERSION=3.2.2
ENV HADOOP_HOME=/opt/hadoop/hadoop-$VERSION
ADD https://downloads.apache.org/hadoop/common/hadoop-$VERSION/hadoop-$VERSION.tar.gz /opt/hadoop/
RUN tar -xzf /opt/hadoop/hadoop-$VERSION.tar.gz -C /opt/hadoop && rm /opt/hadoop/hadoop-$VERSION.tar.gz
ENV LD_LIBRARY_PATH=$HADOOP_HOME/lib/native
COPY build/libs/*.jar /opt/event-aggregator/
WORKDIR /opt
