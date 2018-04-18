package org.manlier.srapp.history;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HistoryServiceTest {

    @Autowired
    HistoryService historyService;

    @Before
    public void setUp() {
        historyService.init();
    }

    @Test
    public void testProduceHistoryRecord() throws InterruptedException {
        historyService.addHistoryRecord("0,A10,A12");
        TimeUnit.SECONDS.sleep(60);
    }
}
