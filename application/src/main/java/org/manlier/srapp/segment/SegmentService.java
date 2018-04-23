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

    void updateDictDB(List<Pair<String>> changes);

    List<String> sentenceProcess(String sentence, boolean HMM);

    Optional<Word> searchWord(String name);

    Pair<String> suggestFreq(String... words);

    Pair<String> suggestFreq(String word);

    Pair<String> tuneFreq(String... words);

    Pair<String> tuneFreq(String word);

}
