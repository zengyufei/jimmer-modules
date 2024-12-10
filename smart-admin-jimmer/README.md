# 部署方式

## 1. release 下载部署包

从[release ](https://github.com/zengyufei/jimmer-modules/releases/tag/release)下载 smart-admin-kotlin.jar 和 smart-admin-web.zip。

总共 4 个文件放一起：

	1、 smart-admin-kotlin.jar
	
	2、 smart-admin-web.zip（解压到smart-admin-web文件夹）
	
	3、 docker-compose.yml 
	
	4、 smart_admin_postgres.sql

执行：
```shell
docker-compose up -d
```

## 2.自己构建

### 后端

	1、smart-admin-kotlin 下执行 gradle build -x test

	2、复制 build/libs/smart-admin-kotlin.jar 出来。

### 前端
	1、smart-admin-web-typescript 下执行:
	
	安装依赖：npm install
	
	打包： npm run build:pre

	2、复制 dist 出来，并改名 smart-admin-web。
	
接着参考 【1. release 下载部署包】 部署。