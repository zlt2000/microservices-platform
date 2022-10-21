// @ts-ignore
/* eslint-disable */

declare namespace SEARCH {
  type Indice = {
    docsCount?: string;
    docsDeleted?: string;
    health?: string;
    index?: string;
    status?: string;
    storeSize?: string;
  };

  type SysLog = {
    id?: string;
    '@timestamp'?: Date;
    agent?: { name?: string; type?: string; id?: string; version?: string };
    appName?: string;
    classname?: string;
    id?: string;
    logLevel?: string;
    serverIp?: string;
    serverPort?: string;
    timestamp?: string;
    message?: string;
    traceId?: string;
    spanId?: string;
    threadName?: string;
  };

  type TraceLog = {
    appName?: string;
    parentId?: string;
    serverIp?: string;
    serverPort?: string;
    spanId?: string;
  };

  type AuditLog = {
    id?: string;
    appName?: string;
    className?: string;
    clientId?: string;
    methodName?: string;
    operation?: string;
    timestamp?: string;
    userId?: string;
    userName?: string;
  };

  type SlowSqlLog = {
    id?: string;
    timestamp?: string;
    query_str?: string;
    query_time?: string;
    lock_time?: string;
    rows_sent?: string;
    rows_examined?: string;
  };

  
}
