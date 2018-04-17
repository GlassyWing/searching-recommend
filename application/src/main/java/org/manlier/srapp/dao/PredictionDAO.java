package org.manlier.srapp.dao;

import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.domain.Prediction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionDAO {

    List<Prediction> getPrediction(@Param("userName") String userName
            , @Param("compName") String compName, @Param("num") int num);
}
