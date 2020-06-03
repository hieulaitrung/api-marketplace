FROM openjdk:8-jre-alpine

# Set the location
ENV APP_HOME /usr/app

ENV JVM_XMX 128m

# Copy your fat jar to the container
COPY target/*.jar $APP_HOME/app.jar

COPY src/main/resources/*.properties $APP_HOME/conf/

COPY bin/entrypoint.sh $APP_HOME/bin/entrypoint.sh

RUN chmod a+x $APP_HOME/bin/entrypoint.sh

# Launch
WORKDIR $APP_HOME

ENTRYPOINT ["bin/entrypoint.sh"]
