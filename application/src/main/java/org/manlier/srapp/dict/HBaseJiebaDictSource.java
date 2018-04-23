package org.manlier.srapp.dict;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.manlier.analysis.jieba.dao.DictSource;
import org.manlier.srapp.dao.JiebaDictDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import static org.manlier.srapp.constraints.Schemas.HBaseJiebaDictSchema.*;

@Repository
public class HBaseJiebaDictSource implements DictSource {

    private JiebaDictDAO dictDAO;

    @Autowired
    public HBaseJiebaDictSource(JiebaDictDAO dictDAO) {
        this.dictDAO = dictDAO;
    }

    @Override
    public void loadDict(Charset charset, Consumer<String[]> consumer) throws IOException {
        this.loadDict(consumer);
    }

    @Override
    public void loadDict(Consumer<String[]> consumer) throws IOException {
        dictDAO.loadAll().stream()
                .map(word -> new String[]{word.getName(), String.valueOf(word.getWeight()), word.getTag()})
                .forEach(consumer);
    }
}
