package org.manlier.srapp.dto.result.dict;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.manlier.srapp.domain.Word;
import org.manlier.srapp.dto.result.QueryResult;

import java.util.List;

public class DictQueryResult extends QueryResult<Word> {
    public DictQueryResult(int numFound, int start, List<Word> data) {
        super(numFound, start, data);
    }

    public DictQueryResult(int numFound, List<Word> data) {
        super(numFound, data);
    }

    public DictQueryResult(List<Word> data) {
        super(data);
    }

    @JsonProperty("words")
    @Override
    public List<Word> getData() {
        return super.getData();
    }
}
