package org.manlier.srapp.prediction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.dao.PredictionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PredictionDAOTest {

    @Autowired
    PredictionDAO predictionDAO;

    @Test
    public void testGetPrediction() {
        predictionDAO.getPrediction("0", "B173", 3)
                .forEach(System.out::println);
    }
}
