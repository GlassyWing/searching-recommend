package org.manlier.srapp.thesaurus;

import org.manlier.srapp.common.FileImporter;
import org.manlier.srapp.domain.SynonymsGroup;

import java.util.List;
import java.util.Set;

public interface ThesaurusService extends FileImporter {

    /**
     * 搜索指定词的同义词
     *
     * @param word 单词
     * @return 同义词
     */
    List<SynonymsGroup> searchSynonyms(String word);

    /**
     * 从指定的同义词组中删除一个词
     *
     * @param word    单词
     * @param groupId 同义词组ID
     */
    void deleteWordFromSynonymsGroup(String word, int groupId);


    /**
     * 删除同义词组
     *
     * @param groupId 同义词组ID
     */
    void deleteSynonymsGroup(int groupId);

    /**
     * 添加单词到同义词组中
     *
     * @param words    单词
     * @param groupId 同义词组ID
     */
    void addWordsToSynonymsGroup(Set<String> words, int groupId);

    /**
     * 添加一组同义词
     *
     * @param synonymsGroup 同义词组
     */
    void addSynonymGroup(SynonymsGroup synonymsGroup);


    void addSynonymGroup(String synonyms);

}
