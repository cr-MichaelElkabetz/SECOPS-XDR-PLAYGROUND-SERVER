FROM openjdk:8-jre-alpine
COPY target/xdr-playground-server-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY src/main/resources/static/ /static/
CMD ["java","-jar","app.jar"]
