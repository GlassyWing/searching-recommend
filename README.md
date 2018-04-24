# searching-recommend
基于solr和协同过滤算法的构件检索与推荐系统  

## 简介

> 定义
> 构件：一些可完成特定功能的代码片段和接口，包含构件名称和构件描述等属性，以图形化作为表现形式
> 流程：构件以线性关系进行组合后的以期望能完成更复杂功能的执行过程，以图形化作为表现形式

本项目依托于Hadoop大数据环境（包括HDFS、HBase、Phoenix、Spark、Kafka、Zookeeper、Yarn），借助Solr框架集成jieba分词作为搜索引擎，实现通过同义词进行构件检索。利用Spark ML编写基于物品的协同过滤算法实现构件推荐。

## 项目环境搭建

### Hadoop 环境搭建

hadoop环境借助Ambari进行搭建，Ambari的安装教程见

[Ambari 2.6.x 本地仓库搭建和离线安装]: https://glassywing.github.io/2018/04/01/blog-02/

需要安装的Hadoop环境有如下几项，安装教程见各工具官方文档：

- HDFS
- MapReduce2
- YARN
- HBase
- Spark
- Phoenix
- Zookeeper
- Solr
- hbase-indexer

### 项目依赖

本项目以maven作为依赖管理工具，你需要首先离线安装以下依赖包（这些库不存在于公共仓库，请下载安装，安装教程见链接）,

- [better-jieba](https://github.com/GlassyWing/better-jieba)
- [better-jieba-solr]( https://github.com/GlassyWing/better-jieba-solr)
- [hbase-indexer-phoenix-mapper](https://github.com/GlassyWing/hbase-indexer-phoenix-mapper)

在依赖包完成安装后，下载该项目，若你使用Idea开发工具，将会自动进行maven依赖库的安装。

## 配置说明

### 数据库配置

本项目使用HBase作为数据库，并借助Phoenix进行SQL操作，数据库按照业务逻辑分为三部分

- 构件推荐数据库
- 用户使用历史数据库
- 词库（分词库、同义词库）

数据库定义文件位于`example/sql`目录下，可通过`pysql.sh xxx.sql`进行执行SQL语句。
数据文件位于`example/dict`目录下，通过`psql.py -t tableName localhost data.csv`指令进行导入。

### solr，hbase-indexer配置

#### solr 配置

1. 将位于`example/solr`目录下的`cn_schema_configs`复制到`${SOLR_HOME}/server/solr/configsets`目录下
2. 并将`better-jieba-solr-1.0-SNAPSHOT.jar`、`phoenix-4.13.1-HBase-1.2-client.jar`复制到`${SOLR_HOME}/server/solr-webapp/WEB-INF/lib/`目录下并替换`protobuf-java-3.1.0.jar`为`protobuf-java-2.5.0.jar`
3. 创建集合`compCollection`并指定配置集

```shell
bin/solr create -force -c compCollection \
-n compCollConfigs \
-s 1 \
-rf 1 \
-d cn_schema_configs
```

#### hbase-indexer 配置

1. 将`example/hbase-indexer/morphlines.conf`置于`/conf`目录下
2. 创建索引

```shell
hbase-indexer add-indexer -n compsindexer \
--indexer-conf morphline-phoenix-mapper.xml \
--connection-param solr.zk=localhost:2181/solr \
--connection-param solr.collection=compsCollection \
```

### Kafka 配置

1. 启动Kafka服务
2. 创建topic：history，作为用户使用历史记录的topic

```shell
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic history
```

### application.yml配置

需指定solr服务器地址及参数、zookeeper地址、kafka地址和Kafka Producer、Kafka Comsumer的参数等，具体配置如下所示：

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

## 接口说明