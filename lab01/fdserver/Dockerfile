FROM adoptopenjdk/openjdk11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} fdserver.jar
ENTRYPOINT ["java","-jar","/fdserver.jar"]
COPY /src/main/kotlin/com/fdsystem/fdserver/config/FDInfluxConf.json FDInfluxConf.json
COPY /src/main/kotlin/com/fdsystem/fdserver/config/FDPostgresConf.json FDPostgresConf.json
