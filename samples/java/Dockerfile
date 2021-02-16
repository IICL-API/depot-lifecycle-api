FROM azul/zulu-openjdk-alpine:11.0.6-jre
RUN adduser -D -h /app -u 1000 depotlifecycle
USER depotlifecycle:depotlifecycle
COPY --chown=depotlifecycle build/libs/depotlifecycle-*-all.jar depotlifecycle.jar
EXPOSE 8086
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar depotlifecycle.jar
