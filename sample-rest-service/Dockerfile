FROM openjdk:11-jre
VOLUME /tmp
ADD target/sample-rest-service-*.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]