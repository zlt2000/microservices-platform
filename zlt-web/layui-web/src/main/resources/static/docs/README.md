# EasyWeb 前端开发文档


## 1.简介

> 基于jquery、layui的管理系统模板，单页面、响应式、支持mvvm、极易上手！

* 演示地址：[https://whvse.gitee.io/easywebpage/](https://whvse.gitee.io/easywebpage/login.html)
* 演示账号：随便输 &emsp;&emsp; 密码：随便输 

&emsp;EasyWeb包含前后台，
[前台地址](https://gitee.com/whvse/EasyWebPage)，
[分离版后台地址](https://gitee.com/whvse/EasyWeb)，
[不分离版后台地址](https://gitee.com/whvse/easyweb-shiro)，
后台基于springboot、mybatis、mybatis-plus、Security、OAuth2等，不分离版权限使用shiro。 

&emsp;此文档包含分离和不分离版本的前端开发指南，两者的使用是有些区别的，
不同的地方在文档中都着重指明了，参考文档使用即可。

### 1.1.使用框架 

描述 | 框架 
:---|:---
核心框架 | [Layui](http://www.layui.com/)、[jQuery](http://jquery.cuishifeng.cn/)
路由框架 | [Q.js](https://github.com/itorr/q.js) (纯js轻量级路由框架)
mvvm框架 | [pandyle.js](https://gitee.com/pandarrr/pandyle) (专为jquery编写的mvvm)
主要特色 | 单页面 / 响应式 / 简约 / 极易上手

### 1.2.项目结构

```
|-assets
|     |-css                     // 样式
|     |-images                  // 图片
|     |-libs                    // 第三方库
|
|-pages            // html组件
|     |-system                  // 系统管理页面
|     |-xxxxxx                  // 其他业务页面
|     |-tpl                     // 公用组件
|     |     |-message.html                 // 消息
|     |-console.html            // 主页一
|     |-header.html             // 头部
|     |-side.html               // 侧导航
|
|-module                // js模块 (使用layui的模块开发方式)
|     |-admin.js                // admin模块
|     |-config.js                // config模块
|     |-index.js                // index模块
|
|-index.html            // 主界面
|-login.html            // 登陆界面
```

> 不分离版没有pages目录，不分离版的页面由后台输出，前端只有assets和module


## 2.快速开始

&emsp;&emsp;快速开始之前请确保你已经接触过[layui](http://www.layui.com/doc/)并了解layui的使用，
尤其是layui模块的使用。否则看本文档会比较吃力。

### 2.1.导入项目

1. 直接下载项目，或使用git下载；
2. 使用IDEA（WebStorm）或者HBuilder等前端开发工具进行开发；
3. 运行login.html或者index.html启动：

    ![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswshc48i2j30i80d5jt6.jpg)


### 2.2.添加一个业务界面

> 注意：以下操作针对于分离版本，不分离版本菜单维护在数据库，页面由后台渲染，不需要前端有复杂的操作。

比如你要做一个CMS系统，添加一个文章管理界面：

- **第一步：<br>**
   在pages文件夹下面建一个目录cms，然后新建一个页面article.html
   
   ![添加业务页面示例](https://ws1.sinaimg.cn/large/006a7GCKgy1fswocatj9yj30bm0chglu.jpg)
   
   article.html完整代码：
   
   ```html
    <div class="layui-card">
        <div class="layui-card-header">
            <h2 class="header-title">文章管理</h2>
            <span class="layui-breadcrumb pull-right">
              <a href="#!console">首页</a>
              <a><cite>文章管理</cite></a>
            </span>
        </div>
        <div class="layui-card-body">
            <div class="layui-form toolbar">
                搜索：<input id="art-edit-search" class="layui-input search-input" type="text" placeholder="输入关键字"/>&emsp;
                <button id="art-btn-search" class="layui-btn icon-btn"><i class="layui-icon">&#xe615;</i>搜索</button>
            </div>
    
            <!-- 数据表格 -->
            <table class="layui-table" id="art-table" lay-filter="art-table"></table>
        </div>
    </div>
    
    <!-- 表格操作列 -->
    <script type="text/html" id="art-table-bar">
        <a class="layui-btn layui-btn-primary layui-btn-xs" lay-event="edit">修改</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    </script>
    
    <script>
        layui.use(['table', 'util', 'config'], function () {
            var table = layui.table;
            var config = layui.config;
            var util = layui.util;
    
            //渲染表格
            table.render({
                elem: '#art-table',
                url: config.base_server + 'article.json',
                where: {
                    access_token: config.getToken().access_token
                },
                page: false,
                cols: [[
                    {type: 'numbers'},
                    {field: 'id', sort: true, title: '文章ID'},
                    {field: 'label', sort: true, title: '文章标签'},
                    {field: 'title', sort: true, title: '文章标题'},
                    {field: 'author', sort: true, title: '作者'},
                    {
                        field: 'uploadtime', sort: true, templet: function (d) {
                            return util.toDateString(d.createTime);
                        }, title: '发布时间'
                    },
                    {align: 'center', toolbar: '#art-table-bar', title: '操作'}
                ]]
            });
        });
    
    </script>
    ```
   
- **第二步：<br>**
   在module/config.js里面找到menus变量，添加如下所示：
   
   ![配置menus示例](https://ws1.sinaimg.cn/large/006a7GCKgy1ftg1etm1ahj30i80b4752.jpg)

- **第三步：<br>**
   运行项目，查看效果
   
   ![添加业务界面效果](https://ws1.sinaimg.cn/large/006a7GCKgy1fswpb4ieu3j30vo0i475m.jpg)

### 2.3.添加第三方layui扩展模块

> 分离版本和不分离版本添加layui扩展模块都按如下方式来。

请参考项目里面formSelects的添加方法。
- 第一步： 把下载的模块放在module文件夹下面
- 第二步： 打开index.html <br>

   ```javascript
    layui.config({
        base: 'module/'
    }).extend({
        formSelects: 'formSelects/formSelects-v4'
    }).use(['config', 'admin', 'formSelects'], function () {
        var config = layui.config;
        var admin = layui.admin;
        var formSelects = layui.formSelects;

    });
    ```

&emsp;&emsp;如果你的模块没有文件夹直接在module里面就不需要写extend了，如果你的模块有独立的文件夹，像formSelects一样，
就需要在extend里面明确指出模块的位置。

![添加模块示例](https://ws1.sinaimg.cn/large/006a7GCKgy1fswoa7omxej30bh0cgwes.jpg)


---

## 3.开发指南

### 3.1.开发规范

阅读开发规范之前请先了解前面“项目结构”的介绍。

1. css、图片、第三方lib（layui扩展模块除外）全部放在“/assets/”下面；
2. layui扩展模块放在“module”下面，例如项目里面“formSelects”模块；
3. 页面html放在“pages”下面。

> 注意：不分离版本html不需要放在“pages”下面，随便放，因为是后端渲染，前端只需要访问后端的url。
> 比如我这里不分离版本是放在templates目录下面，templates是SpringBoot默认的视图目录。


### 3.2.入口index.html

index.html是项目的主入口，打开index.html你会看到如下代码:

```javascript
layui.config({
    base: 'module/'
}).extend({
    formSelects: 'formSelects/formSelects-v4'
}).use(['config', 'admin','index'], function () {
    var config = layui.config;
    var admin = layui.admin;
    var index = layui.admin;
    
});
```

&emsp;&emsp;这段代码首先告诉了lauyui扩展模块都位于“module/”下面，然后扩展模块因为formSelects使用了文件夹
存放，所以需要在“extend”中明确指出formSelects模块js的位置。 &emsp;为什么admin.js、config.js不需要写extend，
因为admin和config是直接放在module里面。

&emsp;&emsp;接着index.html里面使用了“config”、“admin”等模块，所以admin模块里面的一些方法也就即刻执行了。
admin提供的默认事件也就生效了。

> 注意：不分离版本去除了config.js，所以index.html不需要加载config。

### 3.3.“config”模块介绍

> 注意：不分离版本去除了config.js，不分离的项目也不需要前端做任何配置和缓存。

“config”模块主要是配置项目的一些基本参数。

名称 | 类型 | 描述 
:---|:--- |:--- 
base_server | 变量 | 服务器接口地址
tableName | 变量 | 本地存储表名（token等都会存储在本地中）
pageTabs | 变量 | 是否开启多标签模式，(**不分离版本在index.js中配置**)
getToken | 方法 | 获取缓存的token
putToken | 方法 | 缓存token
removeToken | 方法 | 清除缓存的token
menus | 变量 | 侧导航菜单数组，侧导航根据menus自动渲染
getUser | 方法 | 获取缓存的用户信息
putUser | 方法 | 缓存用户的信息

注意：因为我这边的后台返回的token是这样的<br>
```json
{
	"access_token": "950a7cc9-5a8a-42c9-a693-40e817b1a4b0",
	"token_type": "bearer",
	"refresh_token": "773a0fcd-6023-45f8-8848-e141296cb3cb",
	"expires_in": 27036,
	"scope": "select"
}
```
&emsp;&emsp;所以我这边的token不是一个字符串存储的，是用json存储的，getToken返回的是一个json对象，
如果你的token只是一个字符串，请修改getToken方法和admin里面的req方法，当然我建议最好
命令你的后台人员按这个json格式返回数据，这个是一个标准的格式。

#### 3.3.1.“config”使用示例
```javascript
layui.use(['config'], function () {
    var config = layui.config;

    var token = config.getToken();  // 获取token
    var access_token = token.access_token;  //获取access_token
    
    config.putToken(xxx);
});
```

#### 3.3.2.“menus”数组介绍
config.js里面的menus格式如下：
```javascript
menus: [{
    name: '主页',
    url: 'javascript:;',
    icon: 'layui-icon-home',
    subMenus: [{
        name: '主页一',
        url: '#!console',
        path: 'console.html'
    }]
}, {
    name: '系统管理',
    icon: 'layui-icon-set',
    url: 'javascript:;',
    subMenus: [{
        name: '用户管理',
        url: '#!user',  // 这里url不能带斜杠
        path: user_search.html,
        auth: 'post:/user/query'
    }, {
        name: '角色管理',
        url: '#!role',
        path: 'system/role.html',
        auth: 'get:/role'
    }]
}, {
    name: '多级菜单',
    url: 'javascript:;',
    icon: 'layui-icon-unlink',
    subMenus: [{
        name: '二级菜单',
        url: 'javascript:;',
        subMenus: [{
            name: '三级菜单',
            url: 'javascript:;'
        }]
    }]
}, {
    name: '一级菜单',
    url: 'javascript:;',
    icon: 'layui-icon-unlink'
},{
    name: '我是隐藏菜单',
    url: '#!userDetail',
    path: 'system/user_detail.html',
    hidden: true
}]
```

&emsp;&emsp;index.js里面会自动使用menus数组渲染左侧导航栏，并且会使用url作为关键字自动注册路由监听。
目前只设定了最多支持三菜单，原因是因为模板引擎渲染无法使用递归，所以写了三层循环，如果你需要更多级的菜单，
继续加循环就可以了，主要是因为无法递归，并不是无法做到无限级。 

- `path` 表示html所在的路径，会在pages目录下面寻找。
- `url` 是路由的关键字，也就是说点击这个菜单，浏览器地址栏的url会变成`/#!xxx`。
- `auth` 表示这个菜单需要什么权限，index.js渲染的时候会自动判断权限，没有权限不会渲染出来，不写auth不会进行判断。
- `hidden` 表示菜单是否渲染到左侧导航栏，比如用户详情界面，不需要渲染到左侧导航，name最好也填写，因为在多标签功能中，
    name是作为选项卡的标题。建议隐藏的菜单都写在最后面，不要写在subMenus里面，当然写在哪都可以实现。
    如何打开隐藏的菜单，直接使用`<a href="#!userDetail"></a>`即可，或者`Q.go('userDetail')`。

&emsp;上面的menus数组已经展示了各种不同的写法，根部不同场景决定某些参数是否填写。

**注意：**<br>
&emsp;&emsp;路由关键字`url`不能带`/`，在EasyWeb1.0版本中，url是以`#!system/xxx`这种格式作为关键字的，这种注册方法
是把`system/`开头的所有url一起注册，但是在2.0版本中使用的是循环menus数组一个一个注册，所以url有`/`就会变成第一种格式，是不正确的。

### 3.4.admin模块介绍

admin模块做了很多的操作，这里只重点介绍admin对外封装的一些操作方法。

> 不分离版本admin.js去掉了hasPerm方法，其他方法可以放心使用。

#### 3.4.1.admin提供的默认事件
使用示例：
```html
<a ew-event="fullScreen">全屏</a>
<a ew-event="flexible">折叠导航</a>
```
&emsp;&emsp;只需要在DOM节点上面添加ew-event="xx"即可，此时这个DOM节点就会自动加入相关的点击事件了。全部事件如下表：

事件 | 描述
:---|:--- 
flexible | 折叠侧导航 
refresh | 刷新主体部分 
back | 浏览器后退
theme | 打开主题设置弹窗 
fullScreen | 全屏切换
leftPage | 左滚动选项卡
rightPage | 右滚动选项卡
closeThisTabs | 关闭当前选项卡
closeOtherTabs | 关闭其他选项卡
closeAllTabs | 关闭全部选项卡
closeDialog | 关闭元素所在的layer弹窗

#### 3.4.2.admin提供的方法
使用示例：
```javascript
layui.use(['admin'], function () {
    var admin = layui.admin;

    admin.flexible(true);    // 折叠侧导航
});
```

全部方法：

方法 | 参数 | 描述
:---|:--- |:--- 
flexible(expand) | true和false | 折叠侧导航 
activeNav(url) | a标签里面的href值 | 设置侧导航栏选中
refresh() | 无 | 刷新主体部分
 | | 
popupRight(path) | html地址 | 右侧弹出弹窗
closePopupRight() | 无 | 关闭右侧弹出
 | | 
popupCenter(object) | 见单独说明 | 中间弹出弹窗
finishPopupCenter() | 无 | 关闭中间弹出弹窗并回调finish方法
closePopupCenter() | 无 | 关闭中间弹出弹窗
 | | 
open(object) | 见单独说明 | 封装layer弹出弹窗
 | | 
req(url, data, success, method) | 见单独说明 | 封装的ajax请求，req只返回json
ajax(object) | 见单独说明 | 封装的ajax请求
 | | 
hasPerm(auth) | 权限标识 | 判断用户是否有权限，不分离版本无此方法
 | | 
putTempData(key, value) | key,value | 缓存临时数据
getTempData(key,) | key | 获取缓存的临时数据
 | | 
rollPage(d) | 方向 | 滚动选项卡tab

<br>

> 首先说明一下admin为什么要封装那么多layer的弹窗，因为admin封装的弹窗都是支持弹窗的内容是一个单独的页面，
并且不是以iframe的方式嵌入页面，也就是单页面的形式。

<br>


##### 3.4.2.1.右侧弹出弹窗popupRight
使用示例：
```javascript
admin.popupRight('pages/tpl/message.html');
```
> 分离版本填写独立的html页面即可，不分离版本填写后台的url(Controller)

“message.html”里面也可以有js代码，如下所示：
```html
<div>
    <ul>
        <li class="layui-this">通知</li>
        <li>私信</li>
        <li>待办</li>
    </ul>
</div>

<script>
    layui.use('element', function () {
        var element = layui.element;

    });
</script>
```

> 注意：因为是单页面，不是iframe，所以页面里面请不要写`<html> <head> <title>`之类的东西。


效果图：

![右侧弹出示例](https://ws1.sinaimg.cn/large/006a7GCKgy1fswkiisvg1j30b406g3z3.jpg)

##### 3.4.2.2.中间弹出弹窗popupCenter
&emsp;&emsp;admin封装的popupCenter虽然没有什么特别的样式，但是带有回调的功能。

使用示例（一般用在表单弹窗，如添加、修改用户等）：
```javascript
admin.popupCenter({
    title: '添加用户',
    path: 'pages/system/user_form.html',
    finish: function () {
        
        // 这个方法就是回调的功能，用户添加成功之后让表格reload
        table.reload('user-table', {});  
        
    }
});
```

**参数说明：**

参数 | 类型 | 是否必须 | 描述
:---|:--- |:--- |:--- 
title | 变量 | 否 | 标题，不写没有标题 
`path` | 变量 | 是 | html路径
`finish` | 方法 | 否 | finish回调
success | 方法 | 否 | html渲染完毕的回调
end | 方法 | 否 | 弹窗关闭的回调
... | ... | ... | 省略更多参数

<br>

> 请注意，除了`path`和`finish`是popupCenter新增的参数，其他参数均与layer的open参数一样，但是`type`和`content`参数无效，
> type固定是1（页面层），content会被path的内容覆盖。

<br>

“user_form.html”内容如下：
```html
<form id="user-form" lay-filter="user-form" class="layui-form model-form">
    <!-- ...省略表单内容 -->
</form>

<script>
    layui.use(['admin', 'form'], function () {
        var admin = layui.admin;
        var form = layui.form;

        // 表单提交事件
        form.on('submit(user-form-submit)', function (data) {
            layer.load(2);
            // 这里是用admin封装的ajax请求
            admin.req('user', data.field, function (data) {
                layer.closeAll('loading');
                if (data.code == 200) {
                    layer.msg('添加成功', {icon: 1});
                    
                    // 这里是关键，调用这个方法就触发finish回调并且关闭弹窗
                    admin.finishPopupCenter();
                    
                } else {
                    layer.msg('添加失败', {icon: 2});
                }
            }, 'POST');
            return false;
        });
    });
</script>
```

示例图：

![中间弹窗示例](https://ws1.sinaimg.cn/large/006a7GCKgy1fswla4k4bmj30b407wmxb.jpg)

完整示例代码：
```javascript
// 全部参数都写
admin.popupCenter({
    title: '添加用户',
    path: 'pages/system/user_form.html',
    finish: function () {
        // finish回调
    },
    success: function() {
      // user_form.html成功渲染到弹窗中
    },
    end: function() {
      // 弹窗关闭
    }
});

// 只写必须参数
admin.popupCenter({path: 'pages/system/user_form.html'});
```

<br>

##### 3.4.2.3.封装的layer弹窗open

&emsp;&emsp;前面讲了admin所封装的弹窗目的是在不使用iframe的前提下支持使用独立的页面，这样便于我们维护代码，减少一个页面里面的代码量。

使用方法：

```javascript
admin.open({
    title: 'xxx',
    path: 'system/user_form.html',
    success: function(){
        
    }
});
```

参数说明：

&emsp;&emsp;`path`是新增的参数，其他参数均为layer.open的参数，但是`type`和`content`参数无效，type固定是1（页面层），
content会被path的内容覆盖，open没有finish方法，popupCenter才有。


<br>

**如何让弹窗出现滚动条？**


&emsp;&emsp;弹窗的默认高度是自适应的，设置了宽高后内容超出是无法出现滚动条的，因为出现弹窗里面的下拉框select下拉会出现滚动条，所以amdin.css里面
禁止了弹窗的滚动条，如果你需要弹窗出现滚动条，请使用如下的方式：
```javascript
admin.open({
    title: 'xxxxx',
    area: ['500px','300px'],
    path: 'system/user/editForm',
    success: function (layero, index) {
        // 关键代码，   ↑↑↑↑↑↑↑↑↑↑↑↑↑（上面的两个参数不要忘了）
        $(layero).children('.layui-layer-content').css('overflow-y', 'scroll');
    }
});
```
只需要在success回调方法里面手动加上样式就可以了。
```javascript
// 写scroll可以防止弹窗内表格出现滚动条
$(layero).children('.layui-layer-content').css('overflow-y', 'scroll');

// 弹窗内没有表格使用auto即可
$(layero).children('.layui-layer-content').css('overflow-y', 'auto');

// 水平垂直都有滚动条使用overflow
$(layero).children('.layui-layer-content').css('overflow', 'auto');
```
admin.popupCenter也同样支持此使用方法

<br>

##### 3.4.2.4.封装的ajax请求req

> 注意：req不支持指定返回的数据类型（json、html、text等），只会返回json的数据。

&emsp;&emsp;admin模块封装的ajax请求会自动传递token（access_token），并且会自动把PUT、DELETE请求转成POST、GET请求
然后加参数_method，因为浏览器不支持PUT、DELETE请求的参数传递，具体原因请百度一下，**不分离版本不会进行此操作**。

&emsp;&emsp;另外分离版本和不分离版本封装的ajax和req都会检验状态码（401登录过期，403没有权限），登录过期会自动跳转到登录界面。

使用示例：

```javascript
// 不传递参数的写法
admin.req('user', {}, function (data) {
    console.log(JSON.stringify(data));
}, 'GET');

// 传递参数的写法
admin.req('user', {
    userId: 'xxx',
    userName: '张三'
}, function (data) {
    console.log(JSON.stringify(data));
}, 'POST');
```

**方法参数说明：**

- 第一个参数： 接口地址，会自动在前面加入config.base_server
- 第二个参数： 传给服务器的参数
- 第三个参数： 请求成功的回调（如果出现http错误404,401等，也会进入这个回调，并且data里面会有code、msg两个参数，
   code是http的错误码，msg是错误信息）
- 第四个参数： 请求的方法（GET、POST、PUT、DELETE）

&emsp;req还会自动判断token是否过期，如果token过期会自动跳转到登录页面，不分离版本是根据code判断401为登录过期。


##### 3.4.2.5.封装的ajax请求ajax

使用方法：
```javascript
admin.ajax({
   url: 'xxxx',
   data: {
       aa:''
   },
   dataType: 'json',
   type: 'POST',
   success: function(result,status,xhr) {
     
   } 
});
```

&emsp;使用方法跟`$.ajax`一模一样，admin封装只是在success之后先判断是否为登录过期和没有权限，然后再执行你的success方法。

&emsp;使用参数也跟`$.ajax`一样，请到[http://www.runoob.com/jquery/ajax-ajax.html](http://www.runoob.com/jquery/ajax-ajax.html)文档中查看`$.ajax`的参数说明。


<br>


##### 3.4.2.6.判断是否有权限hasPerm

> 注意：不分离版本没有此方法。

&emsp;&emsp;这个方法是用来判断当前登录的用户是否有某一权限的操作，使用这个方法的前提是在index.js里面有一个获取
服务器的user信息并使用config.putUser方法缓存，并且user里面包含了权限列表，因为admin会调用config.getUser获取
用户信息从而获取用户的权限列表。

&emsp;&emsp;我这里面服务器返回的用户json信息如下所示，如果你的服务器返回的信息跟下面不一样，请修改hasPerm方法：

```json
{
	"userId": "admin",
	"username": "admin",
	"nickName": "管理员",
	"authorities": [{
		"authority": "get:/role"
	}, {
		"authority": "put:/role"
	}]
}
```

authorities就是用户的权限集合，authority是权限标识。

使用示例，下面的示例是演示没有删除用户的权限隐藏删除按钮：
```html
<div>
    <button id="btn-delete">删除</button>
</div>

<script>
    layui.use(['admin'], function () {
        var admin = layui.admin;
        
        if(!admin.hasPerm('delete:user')) {
            $('#btn-delete').hide();  // remove()也可以
        }
    });
</script>
```

> 如果你担心把按钮隐藏了没有什么卵用，会点技术就可以把按钮在弄出来了，这个担心完全是多余的。
> 因为后台的接口也会有权限验证的，如果没有权限接口会返回{ code: 401, msg: "没有访问权限" }，
> 既然后台限制了，界面为什么还要限制，因为这是需求，如你项目没有隐藏按钮的需求可以不用隐藏。


##### 3.4.2.7.缓存临时数据putTempData
&emsp;&emsp;这个方法是用来把一些临时数据放在session中，页面关闭数据就会失效。  适当使用缓存可以
减少接口请求次数，提升用户体验。

使用示例：
```javascript

admin.putTempData('t_name', '张三');    // 缓存数据

var tName = admin.getTempData('t_name');    // 获取缓存数据

console.log(tName);

```

**使用场景：**

&emsp;&emsp;前面讲了popupCenter弹出添加用户的界面，如果是修改用户，是不是应该传递user的信息呢，
因为修改界面需要回显user的信息，这时就可以用putTempData了，当然也有别的办法传递，这里就不一一演示了。

修改用户按钮的界面（user.html）：
```html
<button id="btn-update">修改用户</button>

<script>
    layui.use(['admin'], function () {
        var admin = layui.admin;
        
        admin.putTempData('t_user', {name: 'xx', sex: 'male'});  // 关键代码
        
        admin.popupCenter({
            title: '修改用户',
            path: 'pages/system/user_form.html',
            finish: function () {
                
            }
        });
    });
</script>
```
修改用户弹窗的界面（user_form.html）：
```html
<form id="user-form" lay-filter="user-form" class="layui-form model-form">
    <!-- ...省略表单内容 -->
</form>

<script>
    layui.use(['admin', 'form'], function () {
        var admin = layui.admin;
        var form = layui.form;

        var user = admin.getTempData('t_user');  // 关键代码
        
        form.val('user-form', user);  //回显数据
    });
</script>
```

##### 3.4.2.8 滚动选项卡 rollPage
使用示例：
```javascript
// 向左滚动
admin.rollPage('left');

// 滚动到当前选中的选项卡
admin.rollPage('auto');

// 向右滚动
admin.rollPage();
```
参数说明：
- left - 向左滚动
- auto - 滚动到当前选项卡
- 其他 - 向右滚动，不写参数就是向右滚动



### 3.5.index模块介绍

&emsp;&emsp;index模块主要是用于加载index.html的
header、side等，获取用户的信息，判断是否开启选项卡改变页面局部等操作，说白了就是用来初始化后台布局的，用于给主体部分的界面
做准备，虽然index.js里面代码行数也不少，但是仔细看就能看懂，注释也都写了，如果你需要
微微修改阅读几遍就可以上手修改了，下面只介绍两个其他页面会用到的封装方法。

> 需要注意的是：分离和不分离版本的index.js和index.html里面的一些写法有很些不同，请注意不要用混淆了。


#### 3.5.1.打开新页面或选项卡Tab

```javascript
layui.use(['index'], function () {
    var index = layui.index;
    
    // 不分离用后台地址
    index.openNewTab({
        title:'个人信息', 
        url:'system/user/myInfo?userId=1',
        menuId: 'myInfo'
    });
    
    // 分离用html页面
    index.openNewTab({
        title: '个人信息', 
        url: 'pages/system/myInfo.html',
        menuId: 'myInfo'
    });
    
    // 当然这行代码一般会写在按钮点击事件里面，直接写在这里就立即打开新页面了
});
```

- `title` 如果开启了多标签，title是选项卡的标题
- `url` 打开的页面地址
- `menuId` 这个参数是一个id标识，浏览器地址栏会变成`#!menuId`。

这个功能的使用场景请见`3.8.1`章节


#### 3.5.2.关闭选项卡

```javascript
layui.use(['index'], function () {
    var index = layui.index;
    
    var menuId = 'myInfo';
    index.closeTab(menuId);
});
```


---



### 3.6.admin提供的css公共类

> 分离和不分离版本css都是一样的，这部分文档就不需要注意什么了，哈哈😄~

#### 3.6.1.辅助类

类名（class） | 说明
:---|:--- 
icon-btn | 带图标的按钮，如果你的按钮用了图标加上类这个更好看 
date-icon | 在元素的右边加入日期的图标 
layui-link | 用于a标签，字体颜色为layui的绿色风格 
layui-text | 用于a标签的上层，a标签字体颜色为蓝色 
pull-right | 右浮动
inline-block | 设置元素display为inline-block

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswq54bfacj307h01ft8h.jpg)

---

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswq5i3hvbj304v01l3ya.jpg)

---

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswq6m1i5zj309p01gt8h.jpg)

---

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswq5ucrwrj309404l3yg.jpg)

```html
<!-- 日期图标 -->
<input class="layui-input date-icon" type="text" placeholder="请选择日期范围"/>

<!-- 图标按钮 -->
<button class="layui-btn icon-btn"><i class="layui-icon layui-icon-search"></i>搜索</button>
<button class="layui-btn icon-btn"><i class="layui-icon layui-icon-add-1"></i>添加</button>

<!-- 绿色超链接 -->
<a href="javascript:;" class="layui-link">帐号注册</a>
<a href="javascript:;" class="layui-link pull-right">忘记密码？</a>

<!-- 蓝色超链接 -->
<div class="layui-text">
    <a href="http://www.layui.com/">layui-v2.3.0</a>
    <a href="https://github.com/itorr/q.js">q.js</a>
    <a href="https://gitee.com/pandarrr/pandyle">pandyle.js</a>
</div>

```


#### 3.6.2.表格上方的工具栏

类名（class） | 说明
:---|:--- 
search-input | 表格上面的输入框样式 
toolbar | 表格上方工具栏样式 

```html
<div class="layui-card-body">

    <div class="layui-form toolbar"> <!-- 关键代码toolbar -->
        搜索：
        <select>
            <option value="">-请选择-</option>
            <option value="user_id">ID</option>
            <option value="username">账号</option>
        </select>&emsp;
        
         <!-- 关键代码search-input -->
        <input class="layui-input search-input" type="text" placeholder="输入关键字"/>&emsp;
        
        <button class="layui-btn icon-btn"><i class="layui-icon">&#xe615;</i>搜索</button>
        <button class="layui-btn icon-btn"><i class="layui-icon">&#xe654;</i>添加</button>
    </div>

    <table class="layui-table" id="user-table" lay-filter="user-table"></table>
</div>
```

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswqb6x89hj30mz098dg2.jpg)


#### 3.6.3.弹窗里面的表单

类名（class） | 说明
:---|:--- 
model-form | 弹窗里面的表单样式 
model-form-footer | 弹窗里面表单底部操作按钮容器的样式 

```html
<form class="layui-form model-form"> <!-- 关键代码model-form -->

    <div class="layui-form-item">
        <label class="layui-form-label">账号</label>
        <div class="layui-input-block">
            <input name="username" placeholder="请输入账号" type="text" class="layui-input"/>
        </div>
    </div>
    
    <div class="layui-form-item">
        <label class="layui-form-label">性别</label>
        <div class="layui-input-block">
            <input type="radio" name="sex" value="男" title="男" checked/>
            <input type="radio" name="sex" value="女" title="女"/>
        </div>
    </div>
    
    <div class="layui-form-item">
        <label class="layui-form-label">角色</label>
        <div class="layui-input-block">
            <select name="roleId" xm-select="roleId" lay-verify="required">
            </select>
        </div>
    </div>
    
     <!-- 关键代码model-form-footer -->
    <div class="layui-form-item model-form-footer">
        <button class="layui-btn layui-btn-primary close" type="button">取消</button>
        <button class="layui-btn" lay-filter="user-form-submit" lay-submit>保存</button>
    </div>
    
</form>
```

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fswqdrhhpvj30h30cnweo.jpg)


#### 3.6.4.完全基于layui后台大布局
&emsp;&emsp;EasyWeb完全基于layui的后台大布局进行样式修改，html结构是完全基于layui的后台大布局的，所以项目里面的
admin.css你可以用于任何layui后台大布局的页面，加入之后你的页面就得到EasyWeb的样式了， 但是侧导航栏
的折叠事件、全屏等事件是写在admin.js里面的，所以建议你直接使用EasyWeb的框架，当然如果你有自己的基于layui后台
大布局的框架，完全可以使用我的admin.css。


### 3.7.鼠标经过自动弹出tips层
使用示例：
```html
<button class="layui-btn" lay-tips="大家好！">按钮</button>
```
&emsp;&emsp;只需要在你的DOM节点上面添加`lay-tips="xxx"`，然后这个节点在鼠标滑过的时候就会自动显示tips层了，简单吧！

**默认提示位置在元素的上面，修改位置可以这样做：**
```html
<button class="layui-btn" lay-tips="大家好！" lay-direction="2">按钮</button>
```
再加一个`lay-direction`属性就可以了，参数如下：
- 1 - 上面，默认，可以不写
- 2 - 右边
- 3 - 下面
- 4 - 左边

效果图：

![](https://ws1.sinaimg.cn/large/006a7GCKgy1fsxm581mpxj309405pa9x.jpg)


### 3.8.路由的使用
&emsp;&emsp;路由这里使用的是Q.js框架，如果你的左侧菜单是配置在config.menus里面的话，不需要自己注册路由监听，
index.js里面会自动帮你注册。

&emsp;&emsp;如果你需要自己添加路由注册监听的话，请参考q.js的开发文档：[Q.js](https://github.com/itorr/q.js)。

**注意：**<br>
&emsp;&emsp;EasyWeb里面使用的q.js进行过修改，我在里面加了一个刷新的方法，所以请不要擅自替换q.js文件，如果你发现
q.js框架出来新版本了，请联系我进行替换，请多多包含，不要嫌麻烦，以免出问题。


#### 3.8.1.打开不在导航栏中的页面

&emsp;&emsp;比如用户详情、个人信息这些页面，它们不在左侧导航中，但是想打开新页面或者选项卡，
又比如添加用户、修改用户这个页面现在是用弹窗的形式，如果想用新页面的形式该怎么实现：

**第一种实现方式：**<br>
&emsp;在分离版本中，你只需要在config.js的menus数组中添加一个隐藏的菜单：
```javascript
menus: [{
    name: '个人信息',
    url: '#!myInfo',
    path: 'system/my_info.html',
    hidden: true
}]
```
html中跳转：
```html
<a href="#!myInfo">个人信息</a>
```
js中跳转：
```javascript
Q.go('myInfo');
```

> 或许你已经注意到了，这种方式跳转的界面是一个固定页面，如果我们想要传递一些参数，
> 它就无法满足，所以有了第二种实现方式。


**第二种实现方式：**<br>
```javascript
layui.use(['index'], function () {
    var index = layui.index;
    
    // 不分离用后台地址
    index.openNewTab({
        title:'个人信息', 
        url:'system/user/myInfo?userId=1',
        menuId: 'myInfo'
    });
    
    // 分离用html页面
    index.openNewTab({
        title: '个人信息', 
        url: 'pages/system/myInfo.html',
        menuId: 'myInfo'
    });
    
    // 当然这行代码一般会写在按钮点击事件里面，直接写在这里就立即打开新页面了
});
```

- `title` 如果开启了多标签，title是选项卡的标题
- `url` 打开的页面地址
- `menuId` 这个参数是一个id标识，如果id一样就只会存在一个tab，也就是说打开一次没有关闭就不会再打开新的页面，
    比如用户详情页面，如果你想userA的详情和userB的详情是两个页面，可以同时存在选项卡中，menuId就不要写一样的。


> 注意：<br>
> 分离版本两种方式都可以用，不分离版本只能用第二种方式，再分离版本中不能使用`?`问号传递参数，建议使用
> 临时缓存的方式传递参数。


请到`3.5.1`和`3.5.2`章节查看跟这个使用场景相关的其他功能方法。


---


### 3.9.mvvm数据绑定、组件等
&emsp;&emsp;虽然现在vue很流行，但是jquery的许多方法仍然很好用，所以现在很多项目里面既有vue，又有jquery，
然而对于开发一个后台管理系统来说，我们仅仅用了vue的数据渲染功能，大部分还是用的jquery的代码，这样一来项目
就显得有点不伦不类了。

&emsp;&emsp;但是对于前后端分离来说，mvvm的框架渲染数据确实很方便，很强大，layui虽然提供了模板引擎，
但是写法别扭，还麻烦，pandyle.js是一个为jquery打造的mvvm框架，还提供了类似vue组件的写法。

&emsp;&emsp;EasyWeb里面的头部header、side等都是使用的它的组件的写法，还有一些下拉框select的渲染也是用的它提供的mvvm的写法。

&emsp;&emsp;我这里就不提供pandyle.js的用法了，以免跟不上pandyle作者的更新脚步，
大家可以到这里[pandyle.js](https://gitee.com/pandarrr/pandyle)查看pandyle的开发文档。

> 注意：<br>
> &emsp;&emsp;不分离版本没有引入pandyle.js，不分离版本的header和side是使用beetl的布局功能实现的，如果你需要在
> 不分离版本中使用mvvm，请自行在idnex.html中引入pandyle.js。


### 3.10.主题功能
&emsp;&emsp;EasyWeb包含前后台，所以开发时间比较紧张，暂时只提供了两套主题，但是提供了一个主题生成器，
请使用主题生成器定制化你的样式：[EasyWeb主题生成器](https://whvse.gitee.io/easywebpage/docs/generater_theme.html)。

&emsp;EasyWeb的主题生成器可以深度定制主题，样式深度到按钮、单选框、下拉框、选项卡等样式。



### 3.11.不分离版本

注意：

&emsp;&emsp;你从EasyWebPage这个地址下载下来的项目是前后端分离版本的，如果你需要前后端不分离版本的页面和后台，
请从[easyweb-shiro](https://gitee.com/whvse/easyweb-shiro)下载。 easyweb-shiro是Java语言开发的，如果你是
php或其他后台语言，想要使用不分离版的页面，请联系作者辅助你结合到你的项目中，因为不分离版本页面跟后台耦合比较大，
分离出静态页面意义不大，所以不分离版本作者就没有独立再维护成纯静态页面了。



### 3.12.树形表格treeTable

在layui的数据表格上进行扩展实现的。

treeTable项目地址：[https://gitee.com/whvse/treetable-lay](https://gitee.com/whvse/treetable-lay)。

- 演示地址： [https://whvse.gitee.io/treetable-lay/](https://whvse.gitee.io/treetable-lay/)

截图：

树形表格1：

![树形表格1](https://ws1.sinaimg.cn/large/006a7GCKly1ftisynlfq0j30ng0g3t9b.jpg)

树形表格2：

![树形表格2](https://ws1.sinaimg.cn/large/006a7GCKgy1ftgdebdnsmj30ux0qktbc.jpg)


## 4.项目截图

![登录](https://ws1.sinaimg.cn/large/006a7GCKgy1fswqs955sdj316v0qmdj1.jpg) 

![主页一](https://ws1.sinaimg.cn/large/006a7GCKgy1fstc7ldhlbj315y0q6415.jpg)

![消息弹窗](https://ws1.sinaimg.cn/large/006a7GCKgy1fstc7lye0jj30vq0i8gmv.jpg)

![角色管理](https://ws1.sinaimg.cn/large/006a7GCKgy1fstc7logerj30vq0i8js2.jpg)

---

## 5.更新日志

- **2018-07-22 - 增加树形表格的功能**

    - 增加树形表格的功能 [treeTable](#_312树形表格treeTable)

- **2018-07-20 - 发布不分离版的easyweb-shiro稳定版本**

    - 不分离版没有config.js，但是同样支持分离版的所有功能
    - 改进路由的注册方法，全部由框架自动完成，开发方式与传统一样，但是可以轻松的使用路由功能提升操作体验

- **2018-07-12 - 增加主题、多标签**
    
    - 增加多标签tab功能，并且增加自由切换是否开启多标签功能
    - 增加主题切换功能，上线 [主题生成器](#_310主题功能) ，自由生成主题样式
    
- **2018-06-28 - 发布全新2.0版本**

    - 引入pandyle.js（mvvm框架），填补layui模板引擎的短板
    - 采用模块化开发方式，定义admin、config等公用模块，封装ajax请求
    - 界面优化，借鉴layadmin的设计风格，改版登录页面

- **2018-02-11 - 发布EasyWeb1.0版本**

    - 基于layui后台大布局、q.js路由框架搭建出第一个版本
    - 1.0 版本在easyweb的gitee附件中下载


## 6.联系方式
### 6.1.欢迎加入“前后端分离技术交流群”
![群二维码](https://ws1.sinaimg.cn/large/006a7GCKgy1fstbxycj1xj305k07m75h.jpg)

### 6.2.我要打赏
&emsp;&emsp;都是猿友，撸码不易，如果这个轮子对你有用，不妨打赏一下！
[码云](https://gitee.com/whvse/EasyWebPage)已开启捐赠功能，谢谢支持！

&emsp;&emsp;EasyWeb目前提供了“路由+mvvm分离版”、“基于oauth2的后台”、“基于shiro的不分离版”、“不分离版的页面”、
“EasyWeb主题生成器”、“前端开发文档”等项目，并且全部开源，欢迎加入一起开发，或者提交pull requests。
