FROM mysql:8.0.32

LABEL maintainer="zhangyingqi <zhulier1124@gmail.com>"

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./rsmanage.sql /docker-entrypoint-initdb.d

COPY ./rsmanage_config.sql /docker-entrypoint-initdb.d

EXPOSE 3306
