package org.manlier.srapp.segment;

import io.reactivex.functions.Consumer;
import org.manlier.analysis.jieba.Pair;
import org.manlier.srapp.dict.DictStateSynService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JiebaDictChangeSensor implements Consumer<List<Pair<String>>> {

    private SegmentService service;
    private DictStateSynService synService;

    public JiebaDictChangeSensor(SegmentService segmentService
    , DictStateSynService synService) {
        this.service = segmentService;
        this.synService = synService;
    }

    @Override
    public void accept(List<Pair<String>> pairs) throws Exception {
        System.out.println(pairs);
        service.updateDictDB(pairs);
        synService.requestSync();
    }
}
