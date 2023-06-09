FROM adoptopenjdk/openjdk11:alpine-jre
LABEL authors="ilk07"
EXPOSE 8888
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar cloudstorage.jar
ENTRYPOINT ["java", "-jar", "cloudstorage.jar"]