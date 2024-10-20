FROM amazoncorretto:17.0.11-alpine3.19

COPY ./Backend/src/main /app/Backend/src/main
COPY ./Backend/pom.xml /app/Backend/pom.xml

COPY ./Frontend/frontend/ /app/Frontend/frontend/

RUN apk add --no-cache maven nodejs npm

RUN npm install -g @angular/cli

ARG APPLICATION_USER=urenaissance
RUN adduser -u 1000 -D $APPLICATION_USER
RUN chown -R $APPLICATION_USER /app/

USER $APPLICATION_USER

WORKDIR /app/Backend/
RUN mvn clean install -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/Backend-0.0.1-SNAPSHOT.jar"]