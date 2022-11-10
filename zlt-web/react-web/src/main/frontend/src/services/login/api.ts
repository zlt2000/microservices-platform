// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

export async function currentUser(options?: { [key: string]: any }) {
  return request<API.OptionalResult<API.CurrentUser>>('/api-user/users/current', {
    method: 'GET',
    ...(options || {}),
  });
}

export async function currentUser2(options?: { [key: string]: any }) {
  const result = await request<API.OptionalResult<API.CurrentUser>>('/api-user/users/current', {
    method: 'GET',
    ...(options || {}),
  });
  return result.datas ?? {};
}

export async function fetchMenuData(options?: { [key: string]: any }) {
  return request<API.Menu[]>('/api-user/menus/current', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 退出登录接口 POST /api/login/outLogin */
export async function outLogin(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api-uaa/oauth/remove/token', {
    method: 'GET',
    ...(options || {}),
  });
}

export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.OptionalResult<API.LoginResult>>('/api-uaa/oauth/token', {
    method: 'POST',
    requestType: 'form',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: 'Basic d2ViQXBwOndlYkFwcA==',
    },
    data: { ...body, grant_type: 'password_code' },
    ...(options || {}),
  });
}
