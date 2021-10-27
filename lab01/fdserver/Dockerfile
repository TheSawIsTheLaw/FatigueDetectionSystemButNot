FROM adoptopenjdk/openjdk11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} fdserver.jar
ENTRYPOINT ["java","-jar","/fdserver.jar"]
COPY /src/main/kotlin/com/fdsystem/fdserver/config/FDConf.json FDConf.json
