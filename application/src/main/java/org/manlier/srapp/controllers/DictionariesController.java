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
import org.manlier.srapp.dto.result.thesaurus.CombineResult;
import org.manlier.srapp.dto.result.thesaurus.SynonymsGroupQueryResult;
import org.manlier.srapp.dto.result.thesaurus.SynonymsQueryResult;
import org.manlier.srapp.segment.SegmentException;
import org.manlier.srapp.segment.SegmentService;
import org.manlier.srapp.storage.StorageService;
import org.manlier.srapp.thesaurus.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
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
     * @param action   动作：共3种
     *                 1.  "cut": 分词
     *                 2.  "tune"  调整字典
     *                 3.   "suggest"   只建议，不调整字典
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
     * @param word 单词
     * @return 响应
     */
    @GetMapping("/api/v1/segment/{word}")
    public ResponseEntity<Result> getWord(@PathVariable("word") String word) {
        Optional<Word> quantum = segmentService.searchWord(word);
        return quantum.<ResponseEntity<Result>>map(word1 -> ResponseEntity.ok(new DictQueryResult(Collections.singletonList(word1))))
                .orElseGet(() -> ResponseEntity.ok(new DictQueryResult(Collections.emptyList())));
    }

    /*=================================同义词字典============================*/

    /**
     * 获得指定单词的同义词组
     *
     * @param word 单词
     * @return 响应
     */
    @GetMapping("/api/v1/thesaurus/synonyms/{word}")
    public ResponseEntity<Result> getSynonymGroups(@PathVariable("word") String word) {
        List<SynonymsGroup> synonymsGroup = thesaurusService.searchSynonyms(word);
        return ResponseEntity.ok(new SynonymsGroupQueryResult(synonymsGroup));
    }

    /**
     * 从同义词组中删除一个单词或删除整个同义词组
     *
     * @param word    单词，若不指定则删除整个同义词组
     * @param groupId 词组ID
     * @return 响应
     */
    @DeleteMapping("/api/v1/thesaurus/synonyms")
    public ResponseEntity deleteWordFromSynonymsGroup(@RequestParam(value = "word", required = false) String word
            , @RequestParam("groupId") Integer groupId) {
        if (word == null) {
            thesaurusService.deleteSynonymsGroup(groupId);
        } else {
            thesaurusService.deleteWordFromSynonymsGroup(word, groupId);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 添加一组词到已有的同义词组中
     *
     * @param words   词组
     * @param groupId 词组ID，若不指定则新建一组同义词组
     * @return 响应
     */
    @PostMapping("/api/v1/thesaurus/synonyms")
    public ResponseEntity<Result> addWordsToGroup(@RequestParam("words") String words
            , @RequestParam(value = "groupId", required = false) Integer groupId) {
        if (groupId == null) {
            thesaurusService.addSynonymGroup(words);
        } else {
            thesaurusService.addWordsToSynonymsGroup(SynonymsConvertor.parseToSet(words), groupId);
        }
        return ResponseEntity.ok(new SynonymsQueryResult(Collections.singletonList(words)));
    }

    /**
     * 组合多组同义词为一组
     *
     * @param map 同义词ID
     * @return 响应
     */
    @PostMapping("/api/v1/thesaurus/synonyms/combine")
    public ResponseEntity<Result> combineSynonymsGroup(@RequestBody Map<String, Integer[]> map) {
        Integer[] groupIds = map.get("groupIds");
        if (groupIds == null) {
            throw new ThesaurusException("Counld not found params: groupIds ");
        }
        SynonymsGroup combination = thesaurusService.combineSynonymsGroups(groupIds);
        return ResponseEntity.ok(new CombineResult(Collections.singletonList(combination)));
    }


    /**
     * 上传同义字典文件，文件格式参照“/resource/comp_synonyms.txt
     *
     * @param files 同义字典文件
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

    @ExceptionHandler(ThesaurusImportException.class)
    public ResponseEntity handleThesaurusImportException(ThesaurusImportException e) {
        Error error = new Error(e.getMessage(), 204);
        return ResponseEntity.badRequest().body(new ErrorResult(error));
    }
}
