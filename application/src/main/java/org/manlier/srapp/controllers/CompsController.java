package org.manlier.srapp.controllers;

import org.apache.log4j.Logger;
import org.manlier.srapp.component.ComponentAlreadyExistsException;
import org.manlier.srapp.component.ComponentNotFoundException;
import org.manlier.srapp.constraints.Error;
import org.manlier.srapp.dto.result.ErrorResult;
import org.manlier.srapp.dto.result.base.*;
import org.manlier.srapp.dto.result.component.ComponentsQueryResult;
import org.manlier.srapp.dto.result.component.ComponentsUpdateResult;
import org.manlier.srapp.entities.Component;
import org.manlier.srapp.component.CompsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.manlier.srapp.constraints.Limits.*;

@RestController
public class CompsController {

    private CompsService service;
    private Logger logger = Logger.getLogger(getClass());

    @Inject
    public CompsController(CompsService service) {
        this.service = service;
    }

    /**
     * 按条件过滤查找构件
     *
     * @param desc 构件描述
     * @param rows 欲得到的构件数
     * @return 响应结果
     */
    @GetMapping(value = "/api/v1/comps")
    public ResponseEntity<Result> searchComps(@RequestParam("desc") String desc
            , @RequestParam(value = "rows", defaultValue = "10") int rows) {
        logger.debug("Try to search components with filter param: desc=" + desc + ", " + "rows=" + rows);
        rows = Math.min(rows, MAX_COMPS_QUERY_NUM);
        logger.debug("Searching components.");
        List<Component> components = service.searchComps(desc, rows);
        ComponentsQueryResult queryResult = new ComponentsQueryResult(components);
        logger.debug("There are " + components.size() + " components found.");
        return ResponseEntity.ok(queryResult);
    }

    /**
     * 按构件id查询构件
     *
     * @param id 构件id
     * @return 响应结果
     */
    @GetMapping(value = "/api/v1/comps/{id:.+}")
    public ResponseEntity<Result> searchComp(@PathVariable("id") String id) {
        logger.debug("Try to find specified component with id: " + id);
        Optional<Component> component = service.searchComp(id);
        ComponentsQueryResult queryResult;
        if (component.isPresent()) {
            queryResult = new ComponentsQueryResult(
                    Collections.singletonList(component.get()));
            logger.debug("Component found.");
        } else {
            queryResult = new ComponentsQueryResult(Collections.emptyList());
        }
        return ResponseEntity.ok(queryResult);
    }

    /**
     * 添加构件
     *
     * @param component 构件
     * @return 响应结果
     */
    @PostMapping(value = "/api/v1/comps")
    public ResponseEntity<Result> addComp(@RequestBody Component component) {
        logger.debug("Adding component: " + component.getId());
        ComponentsQueryResult queryResult = new ComponentsQueryResult(
                Collections.singletonList(service.addComp(component)));
        logger.debug("Component: " + component.getId() + " added.");
        return ResponseEntity.ok(queryResult);
    }

    /**
     * 根据构件id删除一个构件
     *
     * @param id 构件id
     * @return 响应结果
     */
    @DeleteMapping(value = "/api/v1/comps/{id:.+}")
    public ResponseEntity deleteComp(@PathVariable("id") String id) {
        logger.debug("Deleting component: " + id);
        service.deleteComp(id);
        logger.debug("Component: " + id + " has deleted.");
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新一个构件
     *
     * @param component 构件
     * @return 响应结果
     */
    @PatchMapping(value = "/api/v1/comps")
    public ResponseEntity<Result> updateComp(@RequestBody Component component) {
        logger.debug("Updating component: " + component.getId());
        ComponentsUpdateResult result = new ComponentsUpdateResult(
                Collections.singletonList(service.updateComp(component)));
        logger.debug("Update component: " + component.getId() + " has done.");
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(ComponentNotFoundException.class)
    public ResponseEntity handleComponentNotFound(ComponentNotFoundException e) {
        Error error = new Error(e.getMessage(), 101);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResult(error));
    }

    @ExceptionHandler(ComponentAlreadyExistsException.class)
    public ResponseEntity handleComponentAlreadyExists(ComponentAlreadyExistsException e) {
        Error error = new Error(e.getMessage(), 102);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResult(error));
    }
}
