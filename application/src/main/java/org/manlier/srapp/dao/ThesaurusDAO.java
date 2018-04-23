package org.manlier.srapp.dao;

import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.domain.SynonymsGroup;
import org.manlier.srapp.domain.SynonymsGroupStr;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 同意词库包含两张表
 * - thesaurus_group
 * - thesaurus_belong
 * 结构如下所示：
 * - thesaurus_group:
 * -- groupId   (将一组词分组后的分组ID)
 * -- synonyms  (包含一组同义词，以字符串形式保存，以英文逗号分割）
 * - thesaurus_belong:
 * -- word  (单词）
 * -- groupId   (单词所属的同义词组的ID)
 */
@Repository
public interface ThesaurusDAO {

    /**
     * 获得与指定单词同义的词
     *
     * @param word 单词
     * @return 每个字符串均为一组同义词，用','相隔
     */
    List<String> getSynonymsByWord(@Param("word") String word);

    /**
     * 获得指定单词所属的所有同义词组的Id
     *
     * @param word 单词
     * @return 同义词组ID
     */
    Integer getSynonymGroupId(@Param("word") String word);

    /**
     * 通过同义词组id获得一组同义词
     *
     * @param groupId 同义词组 ID
     * @return 同义词组，用','相隔
     */
    String getSynonymsByGroupId(@Param("groupId") int groupId);

    /**
     * 添加单词到同义词组的所属关系
     *
     * @param word    单词
     * @param groupId 同义词组ID
     */
    void addBelong(@Param("word") String word, @Param("groupId") int groupId);

    /**
     * 删除单词到同义词组的所属关系
     *
     * @param word 单词
     */
    void deleteBelong(@Param("word") String word);

    /**
     * 删除同义词组
     *
     * @param groupId 同义词组ID
     */
    void deleteSynonymsGroup(@Param("groupId") int groupId);

    /**
     * 添加一组同义词
     *
     * @param synonymsGroupStr 同义词组
     */
    void addSynonymsGroup(@Param("synonymsGroup") SynonymsGroupStr synonymsGroupStr);

    /**
     * 从所属关系中删除有相同groupId的关系
     *
     * @param groupId 同义词ID
     */
    void deleteBelongGroup(@Param("groupId") int groupId);

    /**
     * 更新同义词组
     *
     * @param groupId  同义词组ID
     * @param synonyms 同义词，以英文逗号(,)隔开
     */
    void updateSynonymsGroup(@Param("groupId") int groupId, @Param("synonyms") String synonyms);
}
