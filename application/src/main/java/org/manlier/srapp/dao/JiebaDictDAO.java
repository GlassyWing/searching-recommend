package org.manlier.srapp.dao;

import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.domain.Word;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JiebaDictDAO {

    List<Word> loadAll();

    Word getWordByName(@Param("name") String name);

    void updateWordWithNoTag(@Param("name") String name, @Param("weight") long weight);

    void updateWord(@Param("name") String name, @Param("weight") long weight, @Param("tag") String tag);
}
