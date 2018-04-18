package org.manlier.srapp.controllers;

import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.dto.result.base.Result;
import org.manlier.srapp.dto.result.history.HistoryQueryResult;
import org.manlier.srapp.history.HistoryFormatException;
import org.manlier.srapp.history.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class HistoryController {

    private HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/api/v1/history")
    public ResponseEntity<Result> addHistoryRecord(@Param("record") String record) {
        historyService.addHistoryRecord(record);
        return ResponseEntity.ok(new HistoryQueryResult(Collections.singletonList(record)));
    }

    @ExceptionHandler(HistoryFormatException.class)
    public ResponseEntity handleHistoryFormatError(HistoryFormatException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
