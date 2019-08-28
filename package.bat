@echo on
@echo =============================================================
@echo $                                                           $
@echo $               ZLT Microservices-Platform                  $
@echo $                                                           $
@echo $                                                           $
@echo $                                                           $
@echo $  ZLT All Right Reserved                                   $
@echo $  Copyright (C) 2019-2050                                  $
@echo $                                                           $
@echo =============================================================
@echo.
@echo off

@title ZLT Microservices-Platform
@color 0e

call mvn clean package -Dmaven.test.skip=true

pause