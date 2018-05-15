DROP TABLE IF EXISTS thesaurus_group;
DROP TABLE IF EXISTS thesaurus_belong;
DROP INDEX IF EXISTS thsrs_bl_idx ON thesaurus_belong;
DROP SEQUENCE IF EXISTS thesaurus_seq;

CREATE TABLE thesaurus_group (
  groupId INTEGER PRIMARY KEY
  , synonyms VARCHAR
) SALT_BUCKETS=3;

CREATE TABLE thesaurus_belong (
  word VARCHAR NOT NULL
  , groupId INTEGER NOT NULL
  CONSTRAINT tb_pk PRIMARY KEY (word, groupId)
) SALT_BUCKETS=3;

CREATE INDEX thsrs_bl_idx ON thesaurus_belong(groupId);
CREATE SEQUENCE thesaurus_seq START 0 INCREMENT BY 1 CACHE 10;