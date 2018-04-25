package org.manlier.srapp.thesaurus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.manlier.srapp.dict.DictStateSynService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ThesaurusChangeSensor implements Consumer<Optional<Void>>, DisposableBean {

    private DictStateSynService synService;
    private PublishSubject<Optional> synSignalPublisher;
    private Disposable disposableForSyn;

    public ThesaurusChangeSensor(DictStateSynService stateSynService) {
        this.synService = stateSynService;
        this.synSignalPublisher = PublishSubject.create();
        disposableForSyn = synSignalPublisher
                .debounce(10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(signal -> synService.requestSync());
    }

    @Override
    public void accept(Optional<Void> signal) throws Exception {
        System.out.println("Thesaurus has changed.");
        synSignalPublisher.onNext(Optional.empty());
    }

    @Override
    public void destroy() throws Exception {
        disposableForSyn.dispose();
    }
}
