FROM openjdk:8u292

LABEL MAINTAINER=Andy
WORKDIR /apps
ADD target/zuul-gateway.jar /apps/
CMD ["java","-jar","zuul-gateway.jar"]
# docker run --name zuul-gateway \
# -e spring_cloud_nacos_server_addr=10.0.0.12:8848 \
# -e zlt_datasource_ip=10.0.0.12 \
# -e zlt_datasource_username=root \
# -e zlt_datasource_password=redhat \
# -e spring_redis_host=10.0.0.12 \
# -d zuul-gateway:4.5