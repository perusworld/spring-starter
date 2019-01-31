FROM openjdk:jre
VOLUME /tmp
ADD target/sample-amqp-service-*.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]