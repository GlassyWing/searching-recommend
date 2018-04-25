package org.manlier.srapp.segment;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.manlier.analysis.jieba.JiebaSegmenter;
import org.manlier.analysis.jieba.Pair;
import org.manlier.analysis.jieba.WordDictionary;
import org.manlier.analysis.jieba.dao.DictSource;
import org.manlier.srapp.dao.JiebaDictDAO;
import org.manlier.srapp.dict.DictLoadException;
import org.manlier.srapp.dict.DictStateSynService;
import org.manlier.srapp.domain.Word;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class JiebaSegmentService implements SegmentService,DisposableBean {

    private DictSource dictSource;

    private JiebaDictDAO dictDAO;

    private JiebaSegmenter segmenter;

    private PublishSubject<Optional> synSignalPublisher;
    private Disposable disposableForSyn;


    @Autowired
    public JiebaSegmentService(DictSource dictSource
            , JiebaDictDAO dictDAO
            , DictStateSynService synService
    ) {
        this.dictSource = dictSource;
        this.dictDAO = dictDAO;
        this.synSignalPublisher = PublishSubject.create();
        disposableForSyn = this.synSignalPublisher
                .debounce(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(signal -> synService.requestSync());
        init();
    }

    private void init() {
        System.setProperty("jieba.defaultDict", "false");
        try {
            WordDictionary.getInstance().loadUserDict(dictSource);
        } catch (IOException e) {
            throw new DictLoadException("Fail to load user dict", e);
        }
        segmenter = new JiebaSegmenter();
        segmenter.subscribe(pairs -> {
            updateDictDB(pairs);
            synSignalPublisher.onNext(Optional.empty());
        });

    }

    @Transactional
    public void updateDictDB(List<Pair<String>> changes) {
        changes.parallelStream()
                .forEach(stringPair -> dictDAO.updateWordWithNoTag(stringPair.key, stringPair.freq.longValue()));
    }

    public List<String> sentenceProcess(String sentence, boolean HMM) {
        return segmenter.sentenceProcess(sentence, HMM);
    }

    @Override
    public Optional<Word> searchWord(String name) {
        Word word = dictDAO.getWordByName(name);
        if (word != null) return Optional.of(word);
        return Optional.empty();
    }

    public Pair<String> suggestFreq(String... words) {
        return suggestFreq(false, words);
    }

    public Pair<String> suggestFreq(String word) {
        return suggestFreq(false, word);
    }

    public Pair<String> tuneFreq(String... words) {
        return suggestFreq(true, words);
    }

    public Pair<String> tuneFreq(String word) {
        return suggestFreq(true, word);
    }

    private Pair<String> suggestFreq(boolean tune, String... words) {
        long freq = segmenter.suggestFreq(tune, words);
        return new Pair<>(String.join("", words), freq);
    }

    private Pair<String> suggestFreq(boolean tune, String word) {
        long freq = segmenter.suggestFreq(tune, word);
        return new Pair<>(word, freq);
    }

    @Override
    public void destroy() throws Exception {
        disposableForSyn.dispose();
    }
}
