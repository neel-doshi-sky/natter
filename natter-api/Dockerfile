FROM openjdk:11
LABEL maintainer="Neel Doshi"
ADD build/libs/natter-api-0.0.1-SNAPSHOT.jar natter-api.jar
ENTRYPOINT ["java", "-jar", "natter-api.jar"]
EXPOSE 8080