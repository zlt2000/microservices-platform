// @ts-ignore
/* eslint-disable */
import { request } from 'umi';

export async function statistic() {
  return request<WELCOME.Statistic>('/api-log/requestStat', {
    method: 'GET',
  });
}
