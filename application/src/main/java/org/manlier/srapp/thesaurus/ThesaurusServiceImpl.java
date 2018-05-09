package org.manlier.srapp.thesaurus;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.manlier.common.parsers.SynonymsRecordParser;
import org.manlier.srapp.common.AbstractFileImporter;
import org.manlier.srapp.dao.ThesaurusDAO;
import org.manlier.srapp.domain.SynonymsGroup;
import org.manlier.srapp.domain.SynonymsGroupStr;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.manlier.srapp.thesaurus.SynonymsConvertor.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ThesaurusServiceImpl
        extends AbstractFileImporter<SynonymsGroup>
        implements ThesaurusService, DisposableBean {

    private ThesaurusDAO thesaurusDAO;

    private PublishSubject<Optional<Void>> dbChangeNotifier;

    private Disposable disposable;

    public ThesaurusServiceImpl(ThesaurusDAO thesaurusDAO, ThesaurusChangeSensor changeSensor) {
        this.thesaurusDAO = thesaurusDAO;
        this.dbChangeNotifier = PublishSubject.create();
        disposable = this.dbChangeNotifier.subscribe(changeSensor);
    }

    @Override
    public List<SynonymsGroup> searchSynonyms(String word) {
        return thesaurusDAO.getSynonymsByWord(word)
                .parallelStream()
                .map(SynonymsGroupStr::toSynonymsGroup)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteWordFromSynonymsGroup(String word, int groupId) {
        SynonymsGroupStr synonymStr = thesaurusDAO.getSynonymsByGroupId(groupId);
        if (synonymStr == null) return;
        Set<String> synonyms = parseToSet(synonymStr.getSynonyms());
        synonyms.remove(word);
        thesaurusDAO.deleteBelong(word, groupId);
        thesaurusDAO.updateSynonymsGroup(groupId, parseToString(synonyms));
        dbChangeNotifier.onNext(Optional.empty());
    }


    @Transactional
    @Override
    public void deleteSynonymsGroup(int groupId) {
        thesaurusDAO.deleteSynonymsGroup(groupId);
        thesaurusDAO.deleteBelongGroup(groupId);
        dbChangeNotifier.onNext(Optional.empty());
    }

    @Transactional
    @Override
    public void addWordsToSynonymsGroup(Set<String> words, int groupId) {

        SynonymsGroupStr synonymsGroupStr = thesaurusDAO.getSynonymsByGroupId(groupId);
        if (synonymsGroupStr == null) {
            throw new SynonymsGroupNotExistException("The synonyms group for groupId: " + groupId + "is not exists.");
        }
        Set<String> synonyms = parseToSet(synonymsGroupStr.getSynonyms());
        synonyms.addAll(words);
        thesaurusDAO.updateSynonymsGroup(groupId, parseToString(synonyms));
        words.forEach(word -> thesaurusDAO.addBelong(word, groupId));
        dbChangeNotifier.onNext(Optional.empty());
    }

    @Override
    public void addSynonymGroup(SynonymsGroup synonymsGroup) {
        addSynonymGroup(synonymsGroup, true, true);
    }

    @Override
    public void addSynonymGroup(String synonyms) {
        Set<String> syns = parseToSet(synonyms);
        if (syns.size() <= 1) {
            throw new ThesaurusFormatException("Synonyms must be contains at least 2 words.");
        }
        this.addSynonymGroup(new SynonymsGroup(syns));
    }

    /**
     * 添加一组同义词
     *
     * @param synonymsGroup 同义词组
     * @param notify        是否启用通知，若启用在添加完后会发送字典变更通知
     * @param addBelongTo   是否添加所属关系
     */
//    @Transactional
    private void addSynonymGroup(SynonymsGroup synonymsGroup, boolean notify, boolean addBelongTo) {

        System.out.println(synonymsGroup.getSynonyms());

        // 检查当前这组同义词中是否已有单词存在于同义词组中，
        // 若存在，则获得同义词组ID
        Integer groupId = null;
        for (String word : synonymsGroup.getSynonyms()) {
            List<Integer> groups = thesaurusDAO.getGroupIdByWord(word);
            if (groups.size() > 0) {
                groupId = groups.get(0);
                break;
            }
        }

        // 将当前这组词加入到已存在的同义词组中
        if (groupId != null) {
            Set<String> existWords = SynonymsConvertor.parseToSet(thesaurusDAO.getSynonymsByGroupId(groupId).getSynonyms());
            existWords.addAll(synonymsGroup.getSynonyms());
            thesaurusDAO.updateSynonymsGroup(groupId, SynonymsConvertor.parseToString(existWords));
            // 需要添加所属关系
            if (addBelongTo) {
                for (String word : synonymsGroup.getSynonyms()) {
                    thesaurusDAO.addBelong(word, groupId);
                }
            }
        } else {
            // 新建一个分组
            SynonymsGroupStr synonymsGroupStr = synonymsGroup.toSynonymsGroupStr();
            thesaurusDAO.addSynonymsGroup(synonymsGroupStr);

            groupId = synonymsGroupStr.getGroupId();

            //  添加所属关系
            for (String word : synonymsGroup.getSynonyms()) {
                thesaurusDAO.addBelong(word, groupId);
            }
        }

        if (notify) dbChangeNotifier.onNext(Optional.empty());
    }

    /**
     * 根据已有的同义词组，重新创建单词所属表
     */
    private void rebuildBelongTo() {
        int offset = 0, limit = 150;
        while (true) {
            List<SynonymsGroupStr> page = thesaurusDAO.getPagedSynonymsGroups(offset, limit);
            if (page.size() > 0) {
                page.parallelStream().map(SynonymsGroupStr::toSynonymsGroup)
                        .forEach(synonymsGroup -> synonymsGroup.getSynonyms().parallelStream()
                                .forEach(word -> thesaurusDAO.addBelong(word, synonymsGroup.getGroupId())));
                offset += limit;
            } else {
                break;
            }
        }
    }


    @Override
    protected Reader<SynonymsGroup> getReader() {

        return paths -> paths
                .flatMap(path -> {
                    try {
                        return Files.readAllLines(path).stream();
                    } catch (IOException e) {
                        throw new ThesaurusImportException("Fail to read file: " + path.getFileName(), e);
                    }
                })
                .parallel()
                .map(s -> {
                    SynonymsRecordParser parser = new SynonymsRecordParser();
                    parser.parse(s);
                    return new SynonymsGroup(new HashSet<>(parser.getSynonyms()));
                });
    }

    @Override
    protected Writer<SynonymsGroup> getWriter() {
        return synonymGroups -> {
            synonymGroups.forEach(synonymsGroup -> {
                addSynonymGroup(synonymsGroup, false, false);
            });
            rebuildBelongTo();
        };
    }

    //    @Transactional
    @Override
    public void importFromFiles(Stream<Path> paths) {
        super.importFromFiles(paths);
        dbChangeNotifier.onNext(Optional.empty());
    }

    @Override
    public void destroy() throws Exception {
        disposable.dispose();
    }
}
