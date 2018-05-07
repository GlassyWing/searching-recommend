package org.manlier.srapp.controllers;

import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.domain.HistoryRecord;
import org.manlier.srapp.domain.TotalFreq;
import org.manlier.srapp.domain.NumOfUsers;
import org.manlier.srapp.dto.result.base.Result;
import org.manlier.srapp.dto.result.history.HistoryQueryResult;
import org.manlier.srapp.dto.result.history.HistoryQueryResultForUser;
import org.manlier.srapp.dto.result.history.TotalFreqsQueryResult;
import org.manlier.srapp.dto.result.history.NumOfUsersQueryResult;
import org.manlier.srapp.history.HistoryFormatException;
import org.manlier.srapp.history.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
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

    @GetMapping("/api/v1/history")
    public ResponseEntity<Result> getHistoryForUser(@Param("userName") String userName
            , @Param("compName") String compName) {
        List<HistoryRecord> historyRecords = historyService.getHistoryForUser(userName, compName);
        return ResponseEntity.ok(new HistoryQueryResultForUser(historyRecords));
    }

    @GetMapping("/api/v1/history/{compName:.+}/quantity")
    public ResponseEntity<Result> getNumOfUsers(@PathVariable("compName") String compName) {
        List<NumOfUsers> numOfUsers = historyService.getNumOfUsers(compName);
        return ResponseEntity.ok(new NumOfUsersQueryResult(numOfUsers));
    }

    @GetMapping("/api/v1/history/total")
    public ResponseEntity<Result> getTotalFreq(@Param("compName") String compName) {
        List<TotalFreq> totalFreqs = historyService.getTotalFreq(compName);
        return ResponseEntity.ok(new TotalFreqsQueryResult(totalFreqs));
    }

    @ExceptionHandler(HistoryFormatException.class)
    public ResponseEntity handleHistoryFormatError(HistoryFormatException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
