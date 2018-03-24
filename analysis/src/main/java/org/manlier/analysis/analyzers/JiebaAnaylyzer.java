package org.manlier.analysis.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.manlier.analysis.jieba.dao.DictSource;
import org.manlier.analysis.jieba.HBaseJiebaTokenizer;

import java.io.IOException;

public class JiebaAnaylyzer extends Analyzer {

    private DictSource dictSource;

    public JiebaAnaylyzer(DictSource dictSource) {
        this.dictSource = dictSource;
    }


    @Override
    protected TokenStreamComponents createComponents(String s) {
        try {
            return new TokenStreamComponents(new HBaseJiebaTokenizer(dictSource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
