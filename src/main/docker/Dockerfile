# FROM frolvlad/alpine-oraclejdk8:slim
FROM openjdk:8
VOLUME /tmp
ADD meet-spring-boot-docker-1.4.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
