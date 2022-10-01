FROM openjdk:11

ADD target/overonix-exchanger-test-0.0.1-SNAPSHOT.jar overonix-exchanger-test.jar

ENTRYPOINT ["java", "-jar","overonix-exchanger-test.jar"]

EXPOSE 8080