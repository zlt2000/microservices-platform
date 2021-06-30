FROM mysql:5.7

LABEL MAINTAINER=Andy

COPY sql/*.sql /docker-entrypoint-initdb.d/
# docker run --name zlt-db -v /data/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=test -d zlt-db:mysql-8.0.25