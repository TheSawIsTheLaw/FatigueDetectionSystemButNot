FROM gradle:4.7.0-jdk8-alpine
RUN gradle bootJar --no-daemon

FROM adoptopenjdk/openjdk11

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} fdserver.jar
ENTRYPOINT ["java","-jar","/fdserver.jar"]