FROM openjdk:11
WORKDIR /app
COPY . .
RUN ./gradlew bootJar
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
