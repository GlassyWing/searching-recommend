# searching-recommend

基于 solr 和协同过滤算法的构件检索与推荐系统

## 简介

> 定义

构件：一些可完成特定功能的代码片段和接口，包含构件名称和构件描述等属性，以图形化作为表现形式

流程：构件以线性关系进行组合后的以期望能完成更复杂功能的执行过程，以图形化作为表现形式

本项目依托于 Hadoop 大数据环境（包括 HDFS、HBase、Phoenix、Spark、Kafka、Zookeeper、Yarn），借助 Solr 框架集成 jieba 分词作为搜索引擎，实现通过同义词进行构件检索。利用 Spark ML 编写基于物品的协同过滤算法实现构件推荐。

## 项目环境搭建

### Hadoop 环境搭建

需要安装以下 hadoop 环境，推荐借助 Ambari 进行搭建，Ambari 的安装教程见

[ambari 2.6.x 本地仓库搭建和离线安装]: https://glassywing.github.io/2018/04/01/blog-02/

需要安装的 Hadoop 环境有如下几项，安装教程见各工具官方文档：

* HDFS 2.7.0 +
* MapReduce2 2.7.0 +
* YARN 2.7.0 +
* HBase 1.2.x
* Spark 2.3.0
* Phoenix   4.13.0
* Zookeeper 3.4.0 +
* Solr  7.0 +
* hbase-indexer

### 项目maven依赖

本项目以 maven 作为依赖管理工具，特别的，你需要首先安装或打包以下依赖包（这些库不存在于公共仓库，请下载安装，安装教程见各工具说明）,

* [better-jieba](https://github.com/GlassyWing/better-jieba)

下载完成后使用`mvn install -DskipTests`安装

* [better-jieba-solr](https://github.com/GlassyWing/better-jieba-solr)

下载完成后使用`mvn package -DskipTests`得到better-jieba-solr-xxx.jar

* [components-recommend](https://github.com/GlassyWing/components-recommend)

下载完成后使用`mvn install -DskipTests`安装

* [hbase-indexer-phoenix-mapper](https://github.com/GlassyWing/hbase-indexer-phoenix-mapper)

下载完成后使用`mvn package -DskipTests`得到hbase-indexer-phoenix-mapper-xxx.jar


在依赖包完成安装或打包后，下载该项目，若你使用 Idea 开发工具，将会自动进行 maven 依赖库的安装。

## 部署说明

### 数据库部署

本项目使用 HBase 作为数据库，并借助 Phoenix 进行 SQL 操作，数据库按照业务逻辑分为三部分

* 构件推荐数据库（构件库，用户库）
* 用户使用历史数据库
* 词库（分词库、同义词库）

数据库定义文件位于`example/sql`目录下。数据文件位于`example/data`目录下。

通过以下指令创建数据表并导入示例数据

```shell
psql.py jieba_dict.sql  recommend.sql  thesaurus.sql
psql.py -t JIEBA_DICT jieba_dict.csv
psql.py -t JIEBA_DICT jieba_dict.csv
psql.py -t THESAURUS_BELONG thesaurus_belong.csv
psql.py -t USERS users.csv
psql.py -t COMPONENTS components.csv
psql.py -t HISTORY history.csv
```

### solr 配置

1.  将位于`example/solr`目录下的`cn_schema_configs`复制到`${SOLR_HOME}/server/solr/configsets`目录下
2.  并将`better-jieba-solr-1.0-SNAPSHOT.jar`、`phoenix-4.13.1-HBase-1.2-client.jar（位于phoenix安装目录下）`复制到`${SOLR_HOME}/server/solr-webapp/WEB-INF/lib/`目录下并替换`protobuf-java-3.1.0.jar`为`protobuf-java-2.5.0.jar`
3.  启动 solr 服务
4.  创建集合`compCollection`并指定配置集

```shell
bin/solr create -force -c compCollection \
-n compCollConfigs \
-s 1 \
-rf 1 \
-d cn_schema_configs
```

### hbase-indexer 配置

1.  将`hbase-indexer-phoenix-mapper-1.0.0.jar`和`phoenix-core-4.13.1-HBase-1.2.jar（位于phoenix安装目录下）`复制到`${HBASE_INDEXER_HOME}\lib`目录下
2.  启动 hbase-indexer 服务
3.  将`example/hbase-indexer/morphlines.conf`复制到`/conf`目录下，没有则创建
4.  创建索引

```shell
hbase-indexer add-indexer -n compsindexer \
--indexer-conf morphline-phoenix-mapper.xml \
--connection-param solr.zk=localhost:2181/solr \
--connection-param solr.collection=compsCollection \
```

### Kafka 配置

1.  启动 Kafka 服务
2.  创建 topic：history，作为用户使用历史记录的 topic

```shell
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic history
```

### application.yml 配置

需指定 solr 服务器地址及参数、zookeeper 地址、kafka 地址和 Kafka Producer、Kafka Comsumer 的参数等，具体配置如下所示：

```yml
kafka:
  history:
    topics:
      - history
    kafka-params-consumer:
      "[bootstrap.servers]": localhost:9092
      "[group.id]": history
      "[key.deserializer]": org.apache.kafka.common.serialization.StringDeserializer
      "[value.deserializer]": org.apache.kafka.common.serialization.StringDeserializer
    kafka-params-producer:
      "[bootstrap.servers]": localhost:9092
      "[key.serializer]": org.apache.kafka.common.serialization.StringSerializer
      "[value.serializer]": org.apache.kafka.common.serialization.StringSerializer
      "[linger.ms]": 1
      "acks": all
      "[batch.size]": 200
      "[client.id]": history-producer
solr:
  address: http://node2.hdp:8983/solr
  connectionTimeout: 10000
  socketTimeout: 60000
  collectionName: compCollection
```
