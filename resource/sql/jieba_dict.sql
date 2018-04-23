DROP TABLE IF EXISTS jieba_dict;
create table jieba_dict
(name varchar primary key, weight bigint, tag varchar)
COLUMN_ENCODED_BYTES = 0 ;