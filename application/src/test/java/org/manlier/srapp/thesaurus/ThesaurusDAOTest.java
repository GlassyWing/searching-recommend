package org.manlier.srapp.thesaurus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.dao.ThesaurusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ThesaurusDAOTest {

    @Autowired
    ThesaurusDAO thesaurusDAO;

    @Test
    public void testRebuildThesaurus() {
        thesaurusDAO.rebuildSynonymsGroupTable();
    }

    @Test
    public void testRebuildSynonymsBelong() {
        thesaurusDAO.createSynonymsBelongTable();
    }
}
