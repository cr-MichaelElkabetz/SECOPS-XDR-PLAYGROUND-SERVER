FROM openjdk:8-jre-alpine
COPY target/cybereason-test-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
CMD ["java","-jar","app.jar"]
