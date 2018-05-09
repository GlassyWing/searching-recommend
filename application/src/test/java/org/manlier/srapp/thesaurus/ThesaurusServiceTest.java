package org.manlier.srapp.thesaurus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.domain.SynonymsGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ThesaurusServiceTest {

    @Autowired
    ThesaurusServiceImpl service;

    @Test
    public void addSynonymsGroup() {
        service.addSynonymGroup(new SynonymsGroup(new HashSet<>(Arrays.asList("黎明", "朝阳"))));
        service.addSynonymGroup(new SynonymsGroup(new HashSet<>(Arrays.asList("黎明", "希望"))));
//        service.searchSynonyms("黎明").forEach(System.out::println);
    }

    @Test
    public void getSynonymsByWord() {
        service.searchSynonyms("黎明").forEach(System.out::println);
    }

    @Test
    public void addWordToSynonymsGroup() {
        service.addWordsToSynonymsGroup(new HashSet<>(Collections.singletonList("朝阳")), 8500);
    }

    @Test
    public void deleteWordFromSynonymsGroup() {
        service.deleteWordFromSynonymsGroup("朝阳", 8500);
    }

    @Test
    public void deleteSynonymsGroup() {
        service.deleteSynonymsGroup(8500);
    }


}
