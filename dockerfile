FROM amazoncorretto:17.0.11-alpine3.19

ARG APP_HOME=app

ARG BACKEND_DIR=Backend
COPY ./Backend/src/main /${APP_HOME}/${BACKEND_DIR}/src/main
COPY ./Backend/pom.xml /${APP_HOME}/${BACKEND_DIR}/pom.xml

ARG FRONT_DIR=Frontend/frontend
ARG FRONT_SRC_DIR=Frontend/frontend
COPY ./${FRONT_SRC_DIR}/ \
    ./${FRONT_SRC_DIR}/angular.json \
    ./${FRONT_SRC_DIR}/package.json \
    ./${FRONT_SRC_DIR}/package-lock.json \
    ./${FRONT_SRC_DIR}/tsconfig*.json \
    /${APP_HOME}/${FRONT_DIR}/

RUN apk add --no-cache maven nodejs npm

RUN npm install -g @angular/cli

ARG APPLICATION_USER=urenaissance
RUN adduser -u 1000 -D $APPLICATION_USER
RUN chown -R $APPLICATION_USER /${APP_HOME}/

USER $APPLICATION_USER

WORKDIR /${APP_HOME}/${FRONT_DIR}/
RUN npm install
RUN ng build --output-path /${APP_HOME}/${BACKEND_DIR}/src/main/resources/static/

WORKDIR /${APP_HOME}/${BACKEND_DIR}/
RUN mvn clean install -DskipTests -Dexec.skip=true

# Clean up
USER root
RUN rm -rf /${APP_HOME}/${FRONT_DIR}/node_modules /${APP_HOME}/${FRONT_DIR}/.angular
RUN npm cache clean --force \
    && npm uninstall -g @angular/cli \
    && npm uninstall -g npm \
    && rm -rf /root/.npm \
    && apk del nodejs npm maven

USER $APPLICATION_USER

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/Backend-0.0.1-SNAPSHOT.jar"]