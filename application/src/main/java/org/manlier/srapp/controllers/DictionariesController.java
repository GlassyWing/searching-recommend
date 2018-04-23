package org.manlier.srapp.controllers;

import org.apache.ibatis.annotations.Param;
import org.manlier.analysis.jieba.Pair;
import org.manlier.srapp.constraints.Error;
import org.manlier.srapp.constraints.StorageDirs;
import org.manlier.srapp.dict.DictLoadException;
import org.manlier.srapp.domain.SynonymsGroup;
import org.manlier.srapp.domain.Word;
import org.manlier.srapp.dto.result.ErrorResult;
import org.manlier.srapp.dto.result.base.Result;
import org.manlier.srapp.dto.result.dict.DictQueryResult;
import org.manlier.srapp.dto.result.segment.SegmentResult;
import org.manlier.srapp.dto.result.segment.TuneResult;
import org.manlier.srapp.dto.result.storage.FilesQueryResult;
import org.manlier.srapp.dto.result.thesaurus.SynonymsQueryResult;
import org.manlier.srapp.dto.result.thesaurus.WordQueryResult;
import org.manlier.srapp.segment.SegmentException;
import org.manlier.srapp.segment.SegmentService;
import org.manlier.srapp.storage.StorageService;
import org.manlier.srapp.thesaurus.ThesaurusFormatException;
import org.manlier.srapp.thesaurus.ThesaurusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
public class DictionariesController {

    private SegmentService segmentService;
    private StorageService<Path> storageService;
    private ThesaurusService thesaurusService;

    public DictionariesController(
            SegmentService segmentService
            , StorageService<Path> storageService
            , ThesaurusService thesaurusService
    ) {
        this.segmentService = segmentService;
        this.storageService = storageService;
        this.thesaurusService = thesaurusService;
    }

    /**
     * 分词服务
     *
     * @param sentence 语句
     * @param action   动作：共2种
     *                 1.  "cut": 分词
     *                 2.  "tune"  调整字典
     * @return 响应
     */
    @PostMapping("/api/v1/segment")
    public ResponseEntity<Result> tuneDict(@Param("sentence") String sentence
            , @Param(value = "action") String action) {
        if (sentence.matches("\\s+")) {
            throw new SegmentException("sentence is empty!");
        }
        String[] parts = sentence.split("\\s+");
        if (parts.length > 2) {
            throw new SegmentException("Num of words should less than 2!");
        }
        Pair<String> suggest;
        switch (action.trim()) {
            case "tune":
                if (parts.length == 1)
                    suggest = segmentService.tuneFreq(sentence);
                else
                    suggest = segmentService.tuneFreq(parts);
                break;
            case "suggest":
                if (parts.length == 1)
                    suggest = segmentService.suggestFreq(parts[0]);
                else
                    suggest = segmentService.suggestFreq(parts);
                break;
            case "cut":
                List<String> words = segmentService.sentenceProcess(sentence, false);
                return ResponseEntity.ok(new SegmentResult(words));
            default:
                throw new SegmentException("Unsupported action: " + action);
        }
        return ResponseEntity.ok(new TuneResult(suggest));
    }

    /**
     * 获得分词库中具体某个单词的信息
     *
     * @param name 单词
     * @return 响应
     */
    @GetMapping("api/v1/segment/word")
    public ResponseEntity<Result> getWord(@Param("name") String name) {
        Optional<Word> quantum = segmentService.searchWord(name);
        return quantum.<ResponseEntity<Result>>map(word -> ResponseEntity.ok(new DictQueryResult(Collections.singletonList(word))))
                .orElseGet(() -> ResponseEntity.ok(new DictQueryResult(Collections.emptyList())));
    }

    /**
     * 获得指定单词的同义词
     *
     * @param word 单词
     * @return 响应
     */
    @GetMapping("/api/v1/thesaurus")
    public SynonymsGroup getSynonyms(@RequestParam("word") String word) {
        SynonymsGroup synonymsGroup = thesaurusService.searchSynonyms(word);
        if (synonymsGroup != null) {
            return synonymsGroup;
        }
        return null;
    }

    /**
     * 添加一组同义词，如果词库中已经有同义词，将会把这组同义词与原有的同义词进行合并
     *
     * @param synonyms 同义词组
     * @return 响应
     */
    @PostMapping("/api/v1/thesaurus")
    public ResponseEntity<Result> addSynonymsGroup(@RequestParam("synonyms") String synonyms) {
        thesaurusService.addSynonymGroup(synonyms);
        return ResponseEntity.ok(new SynonymsQueryResult(Collections.singletonList(synonyms)));
    }

    /**
     * 删除一组同义词
     *
     * @param groupId 同义词组ID
     * @return 响应
     */
    @DeleteMapping("/api/v1/thesaurus")
    public ResponseEntity deleteSynonymsGroup(@RequestParam("groupId") int groupId) {
        thesaurusService.deleteSynonymsGroup(groupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 添加一个单词到同义词组
     *
     * @param word    单词
     * @param groupId 同义词组ID
     * @return 响应
     */
    @PostMapping("/api/v1/thesaurus/word")
    public ResponseEntity<Result> addWordToSynonymsGroup(@RequestParam("word") String word, @RequestParam("groupId") int groupId) {
        thesaurusService.addWordToSynonymsGroup(word, groupId);
        return ResponseEntity.ok(new WordQueryResult(Collections.singletonList(word)));
    }

    /**
     * 从同义词组中删除一个单词
     *
     * @param word    单词
     * @param groupId 词组ID
     * @return 响应
     */
    @DeleteMapping("/api/v1/thesaurus/word")
    public ResponseEntity deleteWordFromSynonymsGroup(@RequestParam("word") String word, @RequestParam("groupId") int groupId) {
        thesaurusService.deleteWordFromSynonymsGroup(word, groupId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 上传同义词文件，文件格式参照“/resource/comp_synonyms.txt
     *
     * @param files 同义词文件
     * @return 响应
     */
    @PostMapping("/api/v1/thesaurus/upload")
    public CompletableFuture<ResponseEntity<Result>> importThesaurus(@RequestParam("files") MultipartFile[] files) {
        return storageService.store(files, StorageDirs.THESAURUS.name())
                .thenApply(pathStream -> {
                    List<Path> paths = pathStream.collect(Collectors.toList());
                    thesaurusService.importFromFiles(paths.stream());
                    return paths;
                }).thenApply(paths -> {
                    storageService.deleteAll(paths.stream());
                    return paths;
                }).handle((paths, throwable) -> {
                    if (throwable != null) {
                        Error error = new Error(throwable.getMessage(), 204);
                        try {
                            storageService.deleteAll(paths.stream());
                        } catch (NullPointerException e) {
//                            e.printStackTrace();
                        }
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResult(error));
                    } else {
                        return ResponseEntity.ok(new FilesQueryResult(paths.stream()
                                .map(path -> path.getFileName().toString())
                                .collect(Collectors.toList())));
                    }
                });
    }

    @ExceptionHandler(SegmentException.class)
    public ResponseEntity handleSegmentException(SegmentException e) {
        Error error = new Error(e.getMessage(), 201);
        return ResponseEntity.badRequest().body(new ErrorResult(error));
    }

    @ExceptionHandler(DictLoadException.class)
    public ResponseEntity handleDictLoadException(DictLoadException e) {
        Error error = new Error(e.getMessage(), 202);
        return ResponseEntity.badRequest().body(new ErrorResult(error));
    }

    @ExceptionHandler(ThesaurusFormatException.class)
    public ResponseEntity handleThesaurusStateException(ThesaurusFormatException e) {
        Error error = new Error(e.getMessage(), 203);
        return ResponseEntity.badRequest().body(new ErrorResult(error));
    }
}
