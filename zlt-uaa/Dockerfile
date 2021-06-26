FROM openjdk:8u292

LABEL MAINTAINER=Andy
WORKDIR /apps
ADD target/zlt-uaa.jar /apps/
CMD ["java","-jar","zlt-uaa.jar"]
# docker run --name zlt-uaa -e MYSQL_ROOT_PASSWORD=test -d zlt-db:mysql-8.0.25