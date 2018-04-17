package org.manlier.srapp.controllers;

import org.manlier.srapp.domain.Prediction;
import org.manlier.srapp.dto.result.base.Result;
import org.manlier.srapp.dto.result.recommend.PredictionResult;
import org.manlier.srapp.recommend.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendController {

    private RecommendService service;

    @Autowired
    public RecommendController(RecommendService service) {
        this.service = service;
    }

    @GetMapping(value = "/api/v1/recommend")
    public ResponseEntity<Result> recommend(@RequestParam("uuid") String uuid
            , @RequestParam("compName") String compName, @RequestParam("num") int num) {
        List<Prediction> result = service.recommendForUser(uuid, compName, num);
        return ResponseEntity.ok(new PredictionResult(result));
    }
}
