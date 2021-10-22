# runs with sudo docker-compose build --build-arg CACHE_ENABLE=4237823487234
ARG CACHE_ENABLE=1

FROM adoptopenjdk/openjdk11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} fdserver.jar
ENTRYPOINT ["java","-jar","/fdserver.jar"]