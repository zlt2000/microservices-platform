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

export async function saveOrUpdateUser(data: SYSTEM.User): Promise<API.Result<SYSTEM.User>> {
  const result = await request<API.Result<SYSTEM.User>>('/api-user/users/saveOrUpdate', {
    method: 'POST',
    data,
  });
  return result;
}

export async function updateEnabled(id: number, enabled: boolean) {
  const result = await request<API.Result<SYSTEM.User>>('/api-user/users/updateEnabled', {
    method: 'GET',
    params: { id, enabled },
  });
  return result;
}

export async function resetPassword(id: number) {
  const url = `/api-user/users/${id}/password`;
  const result = await request<API.Result<SYSTEM.User>>(url, {
    method: 'PUT',
  });
  return result;
}

export async function deleteUser(id: number) {
  const url = `/api-user/users/${id}`;
  const result = await request<API.Result<void>>(url, {
    method: 'DELETE',
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

export async function importUser(formData: FormData) {
  const result = await request<API.Result<void>>("/api-user/users/import", {
    method: 'POST',
    requestType: 'form',
    data: formData,
  });
  return result;
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

export async function saveOrUpdateRole(data: SYSTEM.Role): Promise<API.Result<SYSTEM.Role>> {
  const result = await request<API.Result<SYSTEM.Role>>('/api-user/roles/saveOrUpdate', {
    method: 'POST',
    data,
  });
  return result;
}

export async function deleteRole(id: number) {
  const url = `/api-user/roles/${id}`;
  const result = await request<API.Result<void>>(url, {
    method: 'DELETE',
  });
  return result;
}

export async function menuForAuth(roleId: number, tenantId: string) {
  const url = `/api-user/menus/${roleId}/menus`;
  const result = await request<SYSTEM.Menu[]>(url, {
    method: 'GET',
    params: { tenantId },
  });
  return result;
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

export async function deleteApp(id: number) {
  const url = `/api-uaa/clients/${id}`
  await request<void>(url, {
    method: 'DELETE',
  });
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

export async function assignMenu(assignMenu: SYSTEM.AssignMenu) {
  const result = await request<API.Result<void>>("/api-user/menus/granted", {
    method: 'POST',
    data: assignMenu,
  });
  return result;
}
