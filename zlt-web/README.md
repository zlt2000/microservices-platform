# Central-Platform UI

 本模块是基于`企业级微服务框架`后端 API 所开发的前端项目。

采用[Ant Design Pro](https://pro.ant.design/)框架，基于[UmiJS v3.x](https://v3.umijs.org/)和[ProComponents](https://procomponents.ant.design/);
图表采用[Ant Design Charts](https://charts.ant.design/)。

## 一、目录说明
考虑到react的复杂性，保留了layui版本的前端。
```lua
│  ├─layui-web -- layui前端
│  │  ├─src
│  │  │  ├─main
│  │  │  │  ├─java -- java启动类
│  │  │  │  ├─resources -- 前端源码
│  │  │  │  │  ├─static -- 前端源码
│  ├─react-web -- react前端
│  │  ├─src
│  │  │  ├─main
│  │  │  │  ├─frontend -- 前端源码(Ant Design Pro)
│  │  │  │  ├─java -- java启动类(集成frontend-maven-plugin)
```

&nbsp;
## 二、运行方式
### 2.1. layui-web
**以下 2 种运行方式，选一种运行即可。**

#### 2.1.1. 方式一：直接运行
运行类 `com.central.web.BackWebApplication`
> 基于 SpringBoot 内嵌的 web 容器

#### 2.1.2. 方式二：静态服务器运行
把 `layui-web\src\main\resources\static` 下的内容复制到类似 `Nginx` 之类的静态服务器运行。

#### 2.1.3. 后端接口地址修改
修改 `layui-web\src\main\resources\static\module\apiUrl.js` 中的地址。

&nbsp;
### 2.2. react-web
**以下 3 种运行方式，选一种运行即可。**

- 需要先安装 `nodejs` 官网地址：https://nodejs.org/en/download/


- 然后安装 **node_modules**：

在 `react-web\src\main\frontend` 目录下执行以下命令：

```bash
//设置使用淘宝的镜像源
npm config set registry https://registry.npm.taobao.org/

// 安装
npm install
```


#### 2.2.1. 方式一：本地运行
##### 2.2.1.1. 运行
运行 `frontend\start.bat` 文件或者执行 `npm start` 命令。

运行成功后，浏览器访问：http://localhost:8066

##### 2.2.1.2. 后端接口地址修改
修改 `react-web\src\main\frontend\config\proxy.ts` 中的地址。

&nbsp;
#### 2.2.2. 方式二：静态服务器运行
##### 2.2.2.1. 源码编译
运行 `frontend\build.bat` 文件或者执行 `npm run build` 命令进行编译。

编译成功后，把 `react-web\src\main\frontend\dist` 下的内容复制到类似 `Nginx` 之类的静态服务器运行。

##### 2.2.2.2. 后端接口地址修改
通过反向代理，例如 `Nginx` 的配置如下：
```json
location ~ ^/api-* {
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Real-Port $remote_port;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_pass http://127.0.0.1:9900;
}

location / {
    root /usr/share/nginx/html;
    index index.html;
    expires 7d;
}
```

> 配置了两条路由，页面请求的各个 `api-xxx` 地址重定向到指定的接口地址；其他的则访问静态文件。

&nbsp;

#### 2.2.3. 方式三：使用Java运行
模块已集成Maven插件`frontend-maven-plugin`用于编译前端代码
>由于需要安装前端环境并编译前端代码，**首次运行需要时间较长**。

在 `react-web` 目录下执行 maven 命令 `mvn package` 打包。

在target目录下生成zlt-web-x.x.x.jar（springboot jar）。
```bash
java -jar zlt-web-5.4.0.jar
```

或者直接在IDE中运行 `react-web\src\main\java\ui\ReactUiBootApplication.java` 
> 运行前必需先使用 maven 对 react-web 工程进行编译或打包。

&nbsp;
## 四、More
开发参考
* [Ant Design Pro](https://pro.ant.design/) 开箱即用的中台前端/设计解决方案
* [UmiJS v3.x](https://v3.umijs.org/) 可扩展的企业级前端应用框架
* [Ant Design](https://ant.design/index-cn) 基于 Ant Design 设计体系的 React UI 组件库
* [ProComponents](https://procomponents.ant.design/) 基于 Ant Design 设计规范，提供更高程度的抽象，提供更上层的设计规范
* [Ant Design Charts](https://charts.ant.design/) 简单好用的 React 图表库
