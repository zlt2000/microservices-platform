const FULL_CHARTER = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopgrstuvwxyz';
//应用id
let clientId = 'webApp';
//授权中心地址
let uaaUri = 'http://127.0.0.1:9900/api-uaa/oauth/';
//端口
let port = 8082;

function getAuthorizeUri() {
    return uaaUri+'authorize?client_id='+clientId+'&redirect_uri=http://127.0.0.1:'+port+'/callback.html&scope=openid&response_type=code';
}

function getLogoutUri(accessToken) {
    return uaaUri+'remove/token?redirect_uri=http://127.0.0.1:'+port+'/index.html&access_token='+accessToken;
}

/**
 * 获取url参数
 */
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return '';
}