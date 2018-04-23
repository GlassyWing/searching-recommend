package org.manlier.srapp.segment;

import io.reactivex.functions.Consumer;
import org.manlier.analysis.jieba.Pair;

import java.util.List;

public class JiebaDictChangeSensor implements Consumer<List<Pair<String>>> {

    private SegmentService service;

    public JiebaDictChangeSensor(SegmentService segmentService) {
        this.service = segmentService;
    }

    @Override
    public void accept(List<Pair<String>> pairs) throws Exception {
        System.out.println(pairs);
        service.updateDictDB(pairs);
    }
}
