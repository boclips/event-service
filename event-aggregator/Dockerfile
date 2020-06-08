FROM openjdk:10-jre-slim
ADD http://apache.mirror.anlx.net/hadoop/common/hadoop-3.2.1/hadoop-3.2.1.tar.gz /opt/hadoop/
RUN tar -xzf /opt/hadoop/hadoop-3.2.1.tar.gz -C /opt/hadoop
ENV HADOOP_HOME=/opt/hadoop/hadoop-3.2.1
ENV LD_LIBRARY_PATH=$HADOOP_HOME/lib/native
COPY build/libs/*.jar /opt/event-aggregator/
WORKDIR /opt
