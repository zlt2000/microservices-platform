// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

export async function indice(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SEARCH.Indice>>('/api-search/admin/indices', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data };
}

export async function index(indexName: string) {
  const result = await request<API.OptionalResult<any>>('/api-search/admin/index', {
    method: 'GET',
    params: { indexName },
  });
  return result.datas;
}

export async function syslog(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SEARCH.SysLog>>('/api-log/sysLog', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function tracelog(traceId: string) {
  const params = { queryStr: `traceId:${traceId}`, sortCol: 'timestamp', sortOrder: 'ASC' };
  const result = await request<SYSTEM.Page<SEARCH.TraceLog>>('/api-log/traceLog', {
    method: 'GET',
    params,
  });
  return result.data;
}

export async function auditlog(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SEARCH.TraceLog>>('/api-log/auditLog', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function slowQueryLog(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SEARCH.SlowSqlLog>>('/api-log/slowQueryLog', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}
