/** logo部分样式 */
.layui-layout-admin .layui-header .layui-logo {
    background-color: {{logo}};
    color: {{logo_font}};
}

/** header样式 */
.layui-layout-admin .layui-header {
    background-color: {{header}};
}

.layui-layout-admin .layui-header a {
    color: {{header_font}};
}

.layui-layout-admin .layui-header a:hover {
    color: {{header_font}};
}

.layui-layout-admin .layui-header .layui-nav .layui-nav-more {
    border-color: {{header_font}} transparent transparent;
}

.layui-layout-admin .layui-header .layui-nav .layui-nav-mored {
    border-color: transparent transparent {{header_font}};
}

/** 导航栏下面的线条 */
.layui-layout-admin .layui-header .layui-nav .layui-this:after, .layui-layout-admin .layui-header .layui-nav-bar {
    background-color: {{header_font}};
}

/** 侧边栏样式 */
.layui-layout-admin .layui-side {
    background-color: {{side}};
}

.layui-nav-tree .layui-nav-child dd.layui-this, .layui-nav-tree .layui-nav-child dd.layui-this a, .layui-nav-tree .layui-this, .layui-nav-tree .layui-this > a, .layui-nav-tree .layui-this > a:hover {
    background-color: {{primary}};
}

.layui-nav-tree .layui-nav-bar {
    background-color: {{primary}};
}

/** 主题颜色 */

/** 按钮 */
.layui-btn:not(.layui-btn-primary):not(.layui-btn-normal):not(.layui-btn-warm):not(.layui-btn-danger):not(.layui-btn-disabled) {
    background-color: {{primary}};
}

.layui-btn.layui-btn-primary:hover {
    border-color: {{primary}};
}

/** 开关 */
.layui-form-onswitch {
    border-color: {{primary}};
    background-color: {{primary}};
}

/** 分页插件 */
.layui-laypage .layui-laypage-curr .layui-laypage-em {
    background-color: {{primary}};
}

.layui-table-page .layui-laypage input:focus {
    border-color: {{primary}} !important;
}

.layui-table-view select:focus {
    border-color: {{primary}} !important;
}

.layui-table-page .layui-laypage a:hover {
    color: {{primary}};
}

/** 单选按钮 */
.layui-form-radio > i:hover, .layui-form-radioed > i {
    color: {{primary}};
}

/** 下拉条目选中 */
.layui-form-select dl dd.layui-this {
    background-color: {{primary}};
}

/** 选项卡 */
.layui-tab-brief > .layui-tab-title .layui-this {
    color: {{primary}};
}

.layui-tab-brief > .layui-tab-more li.layui-this:after, .layui-tab-brief > .layui-tab-title .layui-this:after {
    border-color: {{primary}} !important;
}

/** 面包屑导航 */
.layui-breadcrumb a:hover {
    color: {{primary}} !important;
}