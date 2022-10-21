import type { RequestResponse } from 'umi-request';

export function download(res: RequestResponse): void {
  const blob = new Blob([res.data]); //type是文件类，详情可以参阅blob文件类型
  // 创建新的URL并指向File对象或者Blob对象的地址
  const blobURL = window.URL.createObjectURL(blob);
  // 创建a标签，用于跳转至下载链接
  const tempLink = document.createElement('a');
  tempLink.style.display = 'none';
  tempLink.href = blobURL;
  const disposition = res.response.headers.get('content-disposition');
  const temp: string = disposition ? disposition.split(';')[1].split('=')[1] : 'unkown';
  const filename = temp.substring(1, temp.length - 1);

  tempLink.setAttribute('download', filename);
  // 兼容：某些浏览器不支持HTML5的download属性
  if (typeof tempLink.download === 'undefined') {
    tempLink.setAttribute('target', '_blank');
  }
  // 挂载a标签
  document.body.appendChild(tempLink);
  tempLink.click();
  document.body.removeChild(tempLink);
  // 释放blob URL地址
  window.URL.revokeObjectURL(blobURL);
}
