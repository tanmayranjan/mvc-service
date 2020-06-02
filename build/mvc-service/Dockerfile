FROM openjdk:11.0.7-jre-slim
RUN apt-get update
RUN apt-get install -y  zip \
     unzip \
RUN apt-get install curl wget
RUN adduser -u 1001 -h /home/sunbird/mvcservice -D sunbird \
RUN mkdir -p /home/sunbird/mvcservice

RUN chown -R sunbird:sunbird /home/sunbird/mvcservice
USER sunbird

COPY ./mvc-api/mvc-service/target/mvc-service-1.0-SNAPSHOT.jar /home/sunbird/mvcservice
RUN unzip /home/sunbird/mvc-service-1.0-SNAPSHOT.jar -d /home/sunbird/mvcservice
RUN rm /home/sunbird/mvcservice/mvc-service-1.0-SNAPSHOT.jar
COPY --chown=sunbird ./schemas /home/sunbird/mvcservice/mvc-service-1.0-SNAPSHOT/schemas
WORKDIR /home/sunbird/mvcservice
CMD java -XX:+PrintFlagsFinal $JAVA_OPTIONS  -cp '/home/sunbird/mvcservice/mvc-service-1.0-SNAPSHOT/lib/*' -Dconfig.file=/home/sunbird/mvcservice/mvc-service-1.0-SNAPSHOT/config/application.conf -Dlogger.file=/home/sunbird/mvcservice/mvc-service-1.0-SNAPSHOT/config/logback.xml play.core.server.ProdServerStart /home/sunbird/mvcservice/mvc-service-1.0-SNAPSHOT
