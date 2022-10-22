// 以下代码是配置layui扩展模块的目录，以及加载主题
layui.config({
    base: '/module/'
}).extend({
    formSelects: 'formSelects/formSelects-v4',
    treetable: 'treetable-lay/treetable',
    step: 'step-lay/step'
}).use(['layer'], function () {
    var $ = layui.jquery;
    var layer = layui.layer;

    // 加载设置的主题
    var theme = layui.data('easyweb').theme;
    if (theme) {
        layui.link('/assets/css/theme/' + theme + '.css');
    }

    // 移除loading动画
    setTimeout(function () {
        $('.page-loading').remove();
    }, window == top ? 500 : 300);
});

// 移除主题
function removeTheme() {
    layui.jquery('link[id^=layuicss-assetscsstheme]').remove();
}