FROM eclipse-temurin:21-jre-alpine

COPY target/*.jar fileExchanger.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/fileExchanger.jar"]