FROM openjdk:21-slim

WORKDIR /rsmanage-auth

ARG JAR_FILE=target/rsmanage-auth.jar

COPY ${JAR_FILE} app.jar

EXPOSE 3000

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

# 安装字体相关依赖
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    libfreetype6-dev \
    && rm -rf /var/lib/apt/lists/*

CMD sleep 60; java $JAVA_OPTS -jar app.jar
