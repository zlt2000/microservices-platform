FROM openjdk:8u292

LABEL MAINTAINER=Andy
WORKDIR /apps
ADD target/user-center.jar /apps/
CMD ["java","-jar","user-center.jar"]
# docker run --name user-center \
# -e spring_cloud_nacos_server_addr=10.0.0.12:8848 \
# -e zlt_datasource_ip=10.0.0.12 \
# -e zlt_datasource_username=root \
# -e zlt_datasource_password=redhat \
# -e spring_redis_host=10.0.0.12 \
# -d user-center:4.5