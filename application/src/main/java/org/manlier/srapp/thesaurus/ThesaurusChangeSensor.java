package org.manlier.srapp.thesaurus;

import io.reactivex.functions.Consumer;
import org.manlier.srapp.dict.DictStateSynService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThesaurusChangeSensor implements Consumer<Optional<Void>> {

    private DictStateSynService synService;

    public ThesaurusChangeSensor(DictStateSynService stateSynService) {
        this.synService = stateSynService;
    }

    @Override
    public void accept(Optional<Void> aVoid) throws Exception {
        System.out.println("Thesaurus has changed.");
//        synService.requestSync();
    }
}
