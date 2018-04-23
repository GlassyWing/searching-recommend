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
    public SynonymsGroup searchSynonyms(String word) {
        Integer groupId = thesaurusDAO.getSynonymGroupId(word);
        if (groupId != null) {
            Set<String> synonyms = parseToSet(thesaurusDAO.getSynonymsByGroupId(groupId));
            return new SynonymsGroup(groupId, synonyms);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteWordFromSynonymsGroup(String word, int groupId) {
        String synonymStr = thesaurusDAO.getSynonymsByGroupId(groupId);
        if (synonymStr == null) return;
        Set<String> synonyms = parseToSet(synonymStr);
        synonyms.remove(word);
        thesaurusDAO.deleteBelong(word);
        thesaurusDAO.updateSynonymsGroup(groupId, parseToString(synonyms));
        dbChangeNotifier.onNext(Optional.empty());
    }

    @Transactional
    public void deleteWord(String word) {
        Integer groupId = thesaurusDAO.getSynonymGroupId(word);
        deleteWordFromSynonymsGroup(word, groupId);
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
    public void addWordToSynonymsGroup(String word, int groupId) {
        Integer gid = thesaurusDAO.getSynonymGroupId(word);
        //  如果单词已存在，直接返回
        if (gid != null) return;
        String synonymsStr = thesaurusDAO.getSynonymsByGroupId(groupId);
        Set<String> synonyms = parseToSet(synonymsStr);
        synonyms.add(word);
        thesaurusDAO.updateSynonymsGroup(groupId, parseToString(synonyms));
        thesaurusDAO.addBelong(word, groupId);
        dbChangeNotifier.onNext(Optional.empty());
    }

    @Override
    public void addSynonymGroup(SynonymsGroup synonymsGroup) {
        addSynonymGroup(synonymsGroup, true);
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
     * @param notify        是否启用通知
     */
    @Transactional
    public void addSynonymGroup(SynonymsGroup synonymsGroup, boolean notify) {
        // 查找这组同义词组是否已有单词加入到分组中
        int groupId = findBelongGroupId(synonymsGroup.getSynonyms());
        // 如果已经有分组，则添加到分组中
        if (groupId != -1) {
            synonymsGroup.getSynonyms().addAll(parseToSet(thesaurusDAO.getSynonymsByGroupId(groupId)));
            thesaurusDAO.updateSynonymsGroup(groupId, parseToString(synonymsGroup.getSynonyms()));
        } else {
            //  否则，新建一个分组
            SynonymsGroupStr synonymsGroupStr = synonymsGroup.toSynonymsGroupStr();
            thesaurusDAO.addSynonymsGroup(synonymsGroupStr);
            groupId = synonymsGroupStr.getGroupId();
        }

        //  添加所属关系
        for (String word : synonymsGroup.getSynonyms()) {
            thesaurusDAO.addBelong(word, groupId);
        }

        if (notify) dbChangeNotifier.onNext(Optional.empty());
    }

    private int findBelongGroupId(Set<String> synonyms) {

        for (String word : synonyms) {
            Integer groupId = thesaurusDAO.getSynonymGroupId(word);
            if (groupId != null) {
                return groupId;
            }
        }
        return -1;
    }


    @Override
    protected Reader<SynonymsGroup> getReader() {
        return paths -> {
            SynonymsRecordParser parser = new SynonymsRecordParser();
            return paths
                    .flatMap(path -> {
                        try {
                            return Files.readAllLines(path).stream();
                        } catch (IOException e) {
                            throw new ThesaurusImportException("Fail to read file: " + path.getFileName(), e);
                        }
                    })
                    .parallel()
                    .map(s -> {
                        try {
                            parser.parse(s);
                            return new SynonymsGroup(new HashSet<>(parser.getSynonyms()));
                        } catch (IOException e) {
                            throw new ThesaurusImportException("File format error!", e);
                        }
                    });
        };
    }

    @Override
    protected Writer<SynonymsGroup> getWriter() {
        return synonymGroups ->
                synonymGroups.forEach(synonymsGroup -> addSynonymGroup(synonymsGroup, false));
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
