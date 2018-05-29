package org.manlier.srapp.controllers;

import org.manlier.srapp.component.ComponentAlreadyExistsException;
import org.manlier.srapp.component.ComponentNotFoundException;
import org.manlier.srapp.constraints.Error;
import org.manlier.srapp.constraints.StorageDirs;
import org.manlier.srapp.dto.result.ErrorResult;
import org.manlier.srapp.dto.result.base.*;
import org.manlier.srapp.dto.result.component.ComponentsQueryResult;
import org.manlier.srapp.dto.result.component.ComponentsUpdateResult;
import org.manlier.srapp.domain.Component;
import org.manlier.srapp.component.ComponentService;
import org.manlier.srapp.dto.result.storage.FilesQueryResult;
import org.manlier.srapp.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.manlier.srapp.constraints.Limits.*;

@RestController
@CrossOrigin(origins = "*")
public class ComponentsController {

    private ComponentService service;
    private StorageService<Path> storageService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public ComponentsController(ComponentService service
            , StorageService<Path> storageService) {
        this.service = service;
        this.storageService = storageService;
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
     * 按构件name进行模糊查询
     *
     * @param name 构件name
     * @return 响应结果
     */
    @GetMapping(value = "/api/v1/comps/{name:.+}")
    public ResponseEntity<Result> searchComp(@PathVariable("name") String name) {
        logger.debug("Try to find specified component with id: " + name);
        List<Component> components = service.searchCompLike(name);
        return ResponseEntity.ok(new ComponentsQueryResult(components));
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

    /**
     * 构件导入
     *
     * @param files 构件文件对象
     * @return 导入成功的文件名
     */
    @PostMapping(value = "/api/v1/comps/upload")
    public CompletableFuture<ResponseEntity<Result>> importComps(@RequestParam("files") MultipartFile[] files) {
        logger.debug(Arrays.toString(files));
        return storageService.store(files, StorageDirs.COMPS.name())
                .thenApply(pathStream -> {
                    List<Path> paths = pathStream.collect(Collectors.toList());
                    service.importFromFiles(paths.stream());
                    return paths;
                }).thenApply(paths -> {
                    storageService.deleteAll(paths.stream());
                    return paths;
                }).handle((paths, throwable) -> {
                    if (throwable != null) {
                        Error error = new Error(throwable.getMessage(), 104);
                        storageService.deleteAll(paths.stream());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResult(error));
                    } else {
                        return ResponseEntity.ok(new FilesQueryResult(paths.stream()
                                .map(path -> path.getFileName().toString())
                                .collect(Collectors.toList())));
                    }
                });
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
