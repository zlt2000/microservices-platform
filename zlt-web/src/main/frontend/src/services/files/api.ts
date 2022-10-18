// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

export async function file(params?: { [key: string]: string | number }) {
  const { current, pageSize, ...rest } = params ?? {};
  const result = await request<SYSTEM.Page<FILES.File>>('/api-file/files', {
    method: 'GET',
    params: { page: current, limit: pageSize, ...rest },
  });
  return { data: result.data, total: result.count };
}

export async function deleteFile(id: string) {
  const url = `/api-file/files/${id}`;
  const result = await request<API.Result<void>>(url, {
    method: 'DELETE',
  });
  return result;
}