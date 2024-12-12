FROM openjdk:17
EXPOSE 8089
ADD target/eventsProject-1.0.0-SNAPSHOT.jar eventsProject-1.0.0.jar
ENTRYPOINT ["java","-jar","eventsProject-1.0.0.jar"]