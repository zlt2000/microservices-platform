<div class="layui-card">
    <div class="layui-card-header">
        <h2 class="header-title">${comments}</h2>
        <span class="layui-breadcrumb pull-right">
          <a href="#!console">首页</a>
          <a><cite>${comments}</cite></a>
        </span>
    </div>


    <div class="layui-card-body">
        <div class="layui-form toolbar">
            搜索：
            <input id="${classname}-edit-value" class="layui-input search-input" type="text" placeholder="输入关键字"/>&emsp;
            <button id="${classname}-btn-search" class="layui-btn icon-btn"><i class="layui-icon">&#xe615;</i>搜索</button>
            <button id="${classname}-btn-add" class="layui-btn icon-btn"><i class="layui-icon">&#xe654;</i>添加</button>
        </div>

        <!-- 数据表格 -->
        <table class="layui-table" id="${classname}-table" lay-filter="${classname}-table"></table>
    </div>
</div>


<script>
    layui.use(['form', 'table', 'util', 'config', 'admin'], function () {
        var form = layui.form;
        var table = layui.table;
        var config = layui.config;
        var layer = layui.layer;
        var util = layui.util;
        var admin = layui.admin;

        //渲染表格
        table.render({
            elem: '#${classname}-table',
            url: config.base_server + 'api-${moduleName}/${pathName}/list',
            where: {
                access_token: config.getToken().access_token
            },
            page: true,
            cols: [[
                {type: 'numbers'},
                {field: 'id', sort: true, title: 'ID',width: 80},
                {field: 'name', sort: true, title: 'name'},
                {field: 'code', sort: true, title: 'code'},
                {align: 'center', toolbar: '#${classname}-table-bar', title: '操作',width: 250}
            ]]
        });

    });
</script>