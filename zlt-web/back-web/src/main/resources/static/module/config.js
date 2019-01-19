layui.define(function (exports) {

    var config = {
        base_server: 'http://127.0.0.1:9200/', // 接口地址，实际项目请换成http形式的地址
        tableName: 'easyweb',  // 存储表名
        autoRender: false,  // 窗口大小改变后是否自动重新渲染表格，解决layui数据表格非响应式的问题，目前实现的还不是很好，暂时关闭该功能
        pageTabs: true,   // 是否开启多标签
        // 获取缓存的token
        getToken: function () {
            var t = layui.data(config.tableName).token;
            if (t) {
                return JSON.parse(t);
            }
        },
        // 清除user
        removeToken: function () {
            layui.data(config.tableName, {
                key: 'token',
                remove: true
            });
        },
        // 缓存token
        putToken: function (token) {
            layui.data(config.tableName, {
                key: 'token',
                value: JSON.stringify(token)
            });
        },
        // 导航菜单，最多支持三级，因为还有判断权限，所以渲染左侧菜单很复杂，无法做到递归，你需要更多极请联系我添加，添加可以无限添加，只是无法做到递归
        // 左部菜单
        // menus:
        //     [ {
        //     name: '系统管理',
        //     icon: 'layui-icon-set',
        //     url: 'javascript:;',
        //     subMenus: [{
        //         name: '用户管理',
        //         url: '#!user',  // 这里url不能带斜杠，因为是用递归循环进行关键字注册，带斜杠会被q.js理解为其他注册模式
        //         path: 'system/user.html',
        //         auth: 'back:user:query'
        //     }, {
        //         name: '角色管理',
        //         url: '#!role',
        //         path: 'system/role.html',
        //         auth: 'back:role:query'
        //     },  {
        //         name: '菜单管理',
        //         url: '#!menus',
        //         path: 'system/menus.html',
        //         auth: 'back:menu:query'
        //     }, {
        //         name: '权限管理',
        //         url: '#!permissions',
        //         path: 'system/permissions.html',
        //         auth: 'back:permission:query'
        //     }, {
        //         name: '我的信息',
        //         url: '#!myInfo',
        //         path: 'system/myInfo.html',
        //         hidden :true
        //     }, {
        //         name: 'API文档',
        //         url: '#!api',
        //         path: 'http://127.0.0.1:9200/swagger-ui.html'
        //     }, {
        //         name: '监控中心',
        //         url: '#!monitor',
        //         path: 'http://127.0.0.1:9001/#/wallboard'
        //         //自定义一个字段 携带参数
        //     }
        //     ]
        // }],
        // 当前登录的用户
        getUser: function () {
            var u = layui.data(config.tableName).login_user;
            if (u) {
                return JSON.parse(u);
            }
        },
        // 缓存user
        putUser: function (user) {
            layui.data(config.tableName, {
                key: 'login_user',
                value: JSON.stringify(user)
            });
        }
    };
    exports('config', config);
});
