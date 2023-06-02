FROM openjdk:17-jdk-alpine
WORKDIR opt/app
ARG JAR_FILE=target/Project_Design_I-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]