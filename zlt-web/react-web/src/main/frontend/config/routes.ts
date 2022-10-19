export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        name: 'login',
        path: '/user/login',
        component: './user/Login',
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/system',
    routes: [
      {
        path: '/system/user.html',
        component: './system/User',
      },
      {
        path: '/system/role.html',
        component: './system/Role',
      },
      {
        path: '/system/tokens.html',
        component: './system/Token',
      },
      {
        path: '/system/menus.html',
        component: './system/Menu',
      },
      {
        path: '/system/myInfo.html',
        component: './system/UserInfo',
      },
    ],
  },
  {
    path: '/files',
    routes: [
      {
        path: '/files/files.html',
        component: './files/File',
      }
    ],
  },
  {
    path: '/search',
    routes: [
      {
        path: '/search/index_manager.html',
        component: './search/Index',
      },
      {
        path: '/search/user_search.html',
        component: './search/User',
      },
    ],
  },
  {
    path: '/log',
    routes: [
      {
        path: '/log/sysLog.html',
        component: './log/SysLog',
      },
      {
        path: '/log/auditLog.html',
        component: './log/AuditLog',
      },
      {
        path: '/log/slowQueryLog.html',
        component: './log/SlowSqlLog',
      },
    ],
  },
  {
    path: '/attestation/app.html',
    component: './system/App',
  },
  {
    path: '/generator/list.html',
    component: './system/Generator',
  },
  {
    path: '/welcome',
    name: 'welcome',
    icon: 'smile',
    component: './Welcome',
  },
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    component: './404',
  },
];
