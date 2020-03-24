FROM azul/zulu-openjdk-alpine:11.0.6-jre
COPY build/libs/depotlifecycle-*-all.jar depotlifecycle.jar
EXPOSE 8086
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar depotlifecycle.jar