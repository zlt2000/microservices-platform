# Central-Platform UI

 本模块是基于`企业级微服务框架`后端 API 所开发的前端项目。

采用[Ant Design Pro](https://pro.ant.design/)框架，基于[UmiJS v3.x](https://v3.umijs.org/)和[ProComponents](https://procomponents.ant.design/);
图表采用[Ant Design Charts](https://charts.ant.design/)。

## 1.目录说明
考虑到部署方便，集成了SpringBootWeb,目录结构基于Maven规范。
```lua
│  ├─src -- 源码
│  │  ├─main -- 
│  │  │  ├─frontend -- 前端源码
│  │  │  ├─java -- 后端源码
```
## 2.后端使用说明
模块已集成Maven插件`frontend-maven-plugin`用于编译前端代码
>由于需要安装前端环境并编译前端代码，首次运行需要时间较长
### 打包运行
```bash
mvn package
```
在target目录下生成zlt-web-5.4.0.jar（springboot jar）。
```bash
java -jar zlt-web-5.4.0.jar
```

### IDE中运行

如在本地开发，在运行`SpringBootApplication`前必须先执行
```bash
mvn compile
```

### 配置
 前缀:`zlt.ui`

| 配置项          | 类型     | 是否必须 | 默认值 | 说明    |
|--------------|--------|------|-----|-------|
| path-context | String | 否    | "/" | 上下文路径 |

## 3.前端使用说明
所有前端代码在`frontend`目录下。
### 环境准备

安装 `node_modules`:

```bash
npm install
```

or

```bash
yarn
```

### 运行项目

```bash
npm start
```

### 编译项目

```bash
npm run build
```
### More
开发参考
* [Ant Design Pro](https://pro.ant.design/) 开箱即用的中台前端/设计解决方案
* [UmiJS v3.x](https://v3.umijs.org/) 可扩展的企业级前端应用框架
* [Ant Design](https://ant.design/index-cn) 基于 Ant Design 设计体系的 React UI 组件库
* [ProComponents](https://procomponents.ant.design/) 基于 Ant Design 设计规范，提供更高程度的抽象，提供更上层的设计规范
* [Ant Design Charts](https://charts.ant.design/) 简单好用的 React 图表库
