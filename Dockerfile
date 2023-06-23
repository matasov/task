FROM openjdk:17-alpine
EXPOSE 8080
ARG JAR_FILE=build/libs/task-1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]