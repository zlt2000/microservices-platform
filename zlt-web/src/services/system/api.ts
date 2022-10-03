// @ts-ignore
/* eslint-disable */
import { download } from '@/util/download';
import { request } from 'umi';

export async function user(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SYSTEM.User>>('/api-user/users', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function role() {
  const result = await request<SYSTEM.Page<SYSTEM.Role>>('/api-user/roles', {
    method: 'GET',
  });
  return result.data;
}

export async function pageRole(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SYSTEM.Role>>('/api-user/roles', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function menuForAuth(roleId: number, tenantId: string) {
  const url = `/api-user/menus/${roleId}/menus`;
  const result = await request<SYSTEM.Menu[]>(url, {
    method: 'GET',
    params: { tenantId },
  });
  return result;
}

export function exportUser(params?: { [key: string]: string | number }) {
  return request('/api-user/users/export', {
    method: 'POST',
    params,
    responseType: 'blob',
  }).then((res) => {
    const blob = new Blob([res]);
    const objectURL = URL.createObjectURL(blob);
    let btn = document.createElement('a');
    btn.download = 'user.xls';
    btn.href = objectURL;
    btn.click();
    URL.revokeObjectURL(objectURL);
  });
}

export async function token(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SYSTEM.Token>>('/api-uaa/tokens', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function app(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SYSTEM.App>>('/api-uaa/clients/list', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function generator(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<SYSTEM.Generator>>('/api-generator/generator/list', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export function exportGenerator(tables: string) {
  return request('/api-generator/generator/code', {
    method: 'GET',
    params: { tables },
    responseType: 'blob',
    getResponse: true,
  }).then((res) => {
    download(res);
  });
}

export async function menu(params: { [key: string]: string | number }) {
  const result = await request<SYSTEM.Page<SYSTEM.Menu>>('/api-user/menus/findAlls', {
    method: 'GET',
    params,
  });
  return result.data;
}
