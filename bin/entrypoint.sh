#!/bin/sh
java -Xms32m \
    -Xmx$JVM_XMX \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Ho_Chi_Minh \
    -Duser.language=en \
    -Duser.country=US \
    -Djava.io.tmpdir=/tmp \
    -Dspring.profiles.active=$ENV \
    -jar app.jar \
    --spring.config.location=conf/

