// @ts-ignore
/* eslint-disable */

declare namespace API {
  type Result<T> = {
    resp_code?: number;
    resp_msg?: number;
    datas?: T[];
  };

  type OptionalResult<T> = {
    resp_code?: number;
    resp_msg?: number;
    datas?: T;
  };

  type CurrentUser = SYSTEM.User & {
    permissions: string[];
  };

  type Menu = {
    id?: number;
    name?: string;
    path?: string;
    url?: string;
    css?: string;
    subMenus?: Menu[];
  };

  type LoginResult = {
    access_token?: string;
    token_type?: string;
    refresh_token?: string;
    expires_in?: number;
    scope?: string;
    account_type?: string;
  };

  type LoginParams = {
    username?: string;
    password?: string;
    validCode?: string;
    deviceId?: string;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };
}
