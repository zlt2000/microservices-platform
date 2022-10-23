layui.define(function (exports) {
    /**
     * 用于动态切换环境地址
     */
    //默认地址
    var defUrl = 'http://127.0.0.1:9900/';
    //当前环境的api地址
    var apiUrl;
    try{
        if (my_api_server_url.length > 0) {
            apiUrl = my_api_server_url;
        } else {
            apiUrl = defUrl;
        }
    } catch(e) {
        apiUrl = defUrl;
    }
    var config = {
        base_server: apiUrl,
        tableName: 'easyweb',  // 存储表名
        clientId: 'webApp', // 应用id
        isolationVersion: '', // 隔离版本
        clientSecret: 'webApp', // 应用秘钥
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
