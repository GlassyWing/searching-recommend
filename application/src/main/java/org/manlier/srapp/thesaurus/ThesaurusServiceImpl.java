package org.manlier.srapp.thesaurus;

import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.apache.avro.ipc.trace.ID;
import org.apache.commons.io.FileUtils;
import org.manlier.common.parsers.SynonymsRecordParser;
import org.manlier.srapp.common.AbstractFileImporter;
import org.manlier.srapp.dao.ThesaurusDAO;
import org.manlier.srapp.domain.SynonymsGroup;
import org.manlier.srapp.domain.SynonymsGroupStr;
import org.spark_project.jetty.util.ConcurrentHashSet;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.manlier.srapp.thesaurus.SynonymsConvertor.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ThesaurusServiceImpl
        extends AbstractFileImporter<SynonymsGroup>
        implements ThesaurusService, DisposableBean {

    private ThesaurusDAO thesaurusDAO;

    private PublishSubject<Optional<Void>> dbChangeNotifier;

    private Disposable disposable;

    private Set<Integer> IDs = new ConcurrentHashSet<>();


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
     * @param addBelong     是否添加所属关系
     */
    @Transactional
    void addSynonymGroup(SynonymsGroup synonymsGroup, boolean notify, boolean addBelong) {

        // 新建一个分组
        SynonymsGroupStr synonymsGroupStr = synonymsGroup.toSynonymsGroupStr();
        thesaurusDAO.addSynonymsGroup(synonymsGroupStr);

        Integer groupId = synonymsGroupStr.getGroupId();

        if (addBelong) {
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

    /**
     * 重建同义词字典，并处理冲突
     */
    private void rebuildThesaurus() {
        try {
            Path thesaurusPath = solveConflict();

            thesaurusDAO.rebuildSynonymsGroupTable();
            Files.readAllLines(thesaurusPath).parallelStream()
                    .map(SynonymsGroupStr::new)
                    .map(SynonymsGroupStr::toSynonymsGroup)
                    .forEach(synonymsGroup -> this.addSynonymGroup(synonymsGroup, false, false));
            thesaurusDAO.rebuildSynonymsBelongTable();
            this.rebuildBelongTo();
            IDs.clear();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ThesaurusImportException("Fail to import thesaurus", e);
        }
    }

    /**
     * 处理同义词组间的冲突
     *
     * @return 已处理冲突的同义字典文件路径
     * @throws IOException
     */
    private Path solveConflict() throws IOException {
        int offset = 0, limit = 150;
        Path thesaurusPath = Files.createTempFile("thesaurus", "dict");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(thesaurusPath.toFile(), true))) {
            while (true) {
                List<SynonymsGroup> page = thesaurusDAO
                        .getPagedSynonymsGroups(offset, limit)
                        .parallelStream()
                        .map(SynonymsGroupStr::toSynonymsGroup)
                        .collect(Collectors.toList());
                if (page.size() > 0) {
                    for (SynonymsGroup synonymsGroup : page) {
                        Set<String> synonyms = combineSynonymsGroups(combineGroupIDs(synonymsGroup));
                        if (synonyms.size() > 0) {
                            System.out.println("Size: " + synonyms.size());
                            writer.append(SynonymsConvertor.parseToString(synonyms))
                                    .append("\n");
                        }
                    }
                    writer.flush();
                    offset += limit;
                } else {
                    break;
                }
            }
        }
        return thesaurusPath;

    }

    /**
     * 找到与指定同义词组同义的所有同义词组的ID
     *
     * @param synonymsGroup 同义词组
     * @return 同义词组ID
     */
    private Set<Integer> combineGroupIDs(SynonymsGroup synonymsGroup) {
        if (IDs.contains(synonymsGroup.getGroupId())) {
            return new HashSet<>();
        }
        IDs.add(synonymsGroup.getGroupId());
        HashSet<Integer> groupIds = synonymsGroup.getSynonyms().parallelStream()
                .map(word -> thesaurusDAO.getGroupIdByWord(word))
                .map(HashSet::new)
                .reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                }).orElseGet(HashSet::new);
        Set<Integer> difference = groupIds.parallelStream().filter(groupId -> !IDs.contains(groupId))
                .collect(Collectors.toSet());
//        System.out.println("difference size: " + difference.size());
        groupIds.addAll(difference.parallelStream()
                .map(thesaurusDAO::getSynonymsByGroupId)
                .map(SynonymsGroupStr::toSynonymsGroup)
                .map(this::combineGroupIDs)
                .reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                }).orElseGet(HashSet::new));
        return groupIds;
    }

    /**
     * 将指定的同义词组组合成为一个同义词组
     *
     * @param groupIDs 同义词组ID
     * @return 同义词
     */
    private Set<String> combineSynonymsGroups(Set<Integer> groupIDs) {
        return groupIDs.parallelStream().map(groupId -> thesaurusDAO.getSynonymsByGroupId(groupId))
                .map(SynonymsGroupStr::toSynonymsGroup)
                .map(SynonymsGroup::getSynonyms)
                .reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                }).orElseGet(HashSet::new);
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
            synonymGroups.forEach(synonymsGroup -> addSynonymGroup(synonymsGroup, false, true));
            System.out.println("Initial data loaded, begin to solve conflict!");
            this.rebuildThesaurus();
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
