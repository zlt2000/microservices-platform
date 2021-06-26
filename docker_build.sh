#!/usr/bin/env bash
rm -rf dockerfile/db/sql
rm -rf dockerfile/zlt-uaa/*.jar
rm -rf dockerfile/user-center/*.jar
rm -rf dockerfile/zlt-web/static
rm -rf dockerfile/zuul-gateway/*.jar

cp -avf  zlt-doc/sql dockerfile/db/
cp -avf zlt-uaa/target/zlt-uaa.jar dockerfile/zlt-uaa/
cp -avf zlt-business/user-center/target/user-center.jar dockerfile/user-center
