package org.manlier.srapp.segment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.dao.JiebaDictDAO;
import org.manlier.srapp.dict.HBaseJiebaDictSource;
import org.manlier.srapp.domain.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class JiebaDictDAOTest {

    @Autowired
    JiebaDictDAO dictDAO;

    @Autowired
    HBaseJiebaDictSource dictSource;

    @Test
    public void testLoadAll() {
        List<Word> dict = dictDAO.loadAll();
        System.out.println(dict);
    }

    @Test
    public void testGetWordByName() {
        System.out.println(dictDAO.getWordByName("台北"));;
    }

    @Test
    public void testLoadAllDictSource() throws IOException {
        dictSource.loadDict(strings -> System.out.println(Arrays.toString(strings)));
    }

    @Test
    public void testUpdate() {
        dictDAO.updateWordWithNoTag("what", 5);
        System.out.println(dictDAO.loadAll());
    }
}
