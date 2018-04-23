package org.manlier.srapp.dto.result.segment;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.analysis.jieba.Pair;
import org.manlier.srapp.dto.result.base.Result;

public class TuneResult implements Result<Pair<String>> {

    private Pair<String> result;

    public TuneResult(Pair<String> result) {
        this.result = result;
    }

    @JsonProperty("suggest")
    @Override
    public Pair<String> getData() {
        return result;
    }
}
