package org.manlier.srapp.recommend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.prediction.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PredictionServiceTest {

    @Autowired
    PredictionService predictionService;


    @Test
    public void testMakePrediction() {
        predictionService.makePrediction().show();
    }

    @Test
    public void testStorePrediction() {
        predictionService.storePrediction();
    }
}
