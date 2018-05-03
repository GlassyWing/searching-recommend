package org.manlier.srapp.segment;

import org.manlier.analysis.jieba.Pair;
import org.manlier.srapp.domain.Word;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 分词服务接口
 */
public interface SegmentService {

    /**
     * 更新结巴字典
     *
     * @param changes 发生变更的单词
     */
    void updateDictDB(List<Pair<String>> changes);

    /**
     * 切分一段文字
     *
     * @param sentence 一段语句
     * @param HMM      是否开启隐马尔科夫模型
     * @return 以分割好的单词列表
     */
    List<String> sentenceProcess(String sentence, boolean HMM);

    /**
     * 通过指定的单词名获得单词的比重和词性等信息
     *
     * @param name 单词名
     * @return 单词的比重和词性等信息
     */
    Optional<Word> searchWord(String name);

    /**
     * 获得意图减小单词比重的建议比重
     *
     * @param words 单词
     * @return 发生变更的单词以及比重
     */
    Pair<String> suggestFreq(String... words);

    /**
     * 获得意图加大单词比重的建议比重
     *
     * @param word 单词
     * @return 发生变更的单词以及比重
     */
    Pair<String> suggestFreq(String word);

    /**
     * 减小单词比重
     *
     * @param words 单词
     * @return 发生变更的单词以及比重
     */
    Pair<String> tuneFreq(String... words);

    /**
     * 加大单词比重
     *
     * @param word 单词
     * @return 发生变更的单词以及比重
     */
    Pair<String> tuneFreq(String word);

}
