## 一个基于springboot的论坛项目

## 环境
jdk8
maven
idea
数据库:H2  
前端:thymeleaf

## 插件
lombok  
mybatis generator分页插件：
```xml
<plugin type="org.mybatis.generator.plugins.RowBoundsPlugin"></plugin>
```

## 脚本
```sql
CREATE CACHED TABLE "PUBLIC"."USER"(
    "ID" INT DEFAULT NEXT VALUE FOR "PUBLIC"."SYSTEM_SEQUENCE_3AD0733E_173A_491B_9E33_A8FEC8E8E5F3" NOT NULL NULL_TO_DEFAULT SEQUENCE "PUBLIC"."SYSTEM_SEQUENCE_3AD0733E_173A_491B_9E33_A8FEC8E8E5F3",
    "ACCOUNT_ID" VARCHAR(100),
    "NAME" VARCHAR(32),
    "TOKEN" CHAR(36),
    "GMT_CREATE" BIGINT,
    "GMT_MODIFIED" BIGINT
)
```
##### mybatis-generate的maven命令
```bash
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
```
##### 自动执行DDL语句的命令
```bash
mvn flyway:migrate
```