package org.manlier.srapp.recommend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RecommendServiceTest {

    @Autowired
    RecommendService recommendService;

    @Test
    public void testRecommend() {
        recommendService.recommendForUser("0", "B173", 3)
                .forEach(System.out::println);
    }

    @Test
    public void testMakePrediction() {
        recommendService.makePrediction().show();
    }

    @Test
    public void testStorePrediction() {
        recommendService.storePrediction();
    }
}
