package org.manlier.srapp.history;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.dao.HistoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HistoryDAOTest2 {

    @Autowired
    HistoryDAO historyDAO;

    @Test
    public void testGetNumOfUsers() {
        historyDAO.getNumOfUsers("URL.getParameter")
                .forEach(System.out::println);
    }

    @Test
    public void testGetHistoryForUser() {
        historyDAO.getHistoryForUser("0", "FORM.setFieldDisabled")
                .forEach(System.out::println);
    }

    @Test
    public void testGetNumOfFreq() {
        historyDAO.getTotalFreq("URL.getParameter")
                .forEach(System.out::println);
    }
}
