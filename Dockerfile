FROM openjdk:8-jre-alpine
LABEL version=1 description="EPAM Report portal. Main API Service" maintainer="Andrei Varabyeu <andrei_varabyeu@epam.com>"
ENV JAVA_OPTS="-Xmx1g -Djava.security.egd=file:/dev/./urandom" ARTIFACT=service-api-1-exec.jar APP_DOWNLOAD_URL=https://dl.bintray.com/epam/reportportal/com/epam/reportportal/service-api/1/service-api-1-exec.jar
RUN sh -c "echo $'#!/bin/sh \n\
exec java $JAVA_OPTS -jar $ARTIFACT' > /start.sh && chmod +x /start.sh"
VOLUME ["/tmp"]
RUN wget $APP_DOWNLOAD_URL
EXPOSE 8080
ENTRYPOINT ["/start.sh"]
