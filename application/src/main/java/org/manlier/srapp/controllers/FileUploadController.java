package org.manlier.srapp.controllers;

import org.apache.hadoop.fs.Path;
import org.manlier.srapp.dto.result.base.Result;
import org.manlier.srapp.dto.result.storage.FilesQueryResult;
import org.manlier.srapp.storage.HDFSStorageService;
import org.manlier.srapp.storage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 */
@RestController
public class FileUploadController {

    private HDFSStorageService service;

    @Autowired
    public FileUploadController(HDFSStorageService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/files/{path}")
    public ResponseEntity<Result> listUploadFiles(@PathVariable("path") String path) {

        List<String> files = service.loadAll(path).map(Path::getName).collect(Collectors.toList());
        FilesQueryResult queryResult = new FilesQueryResult(files);
        return ResponseEntity.ok(queryResult);
    }

    @GetMapping("/api/v1/files/{path}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable("path") String path
            , @PathVariable("filename") String filename) {
        Resource file = service.loadAsResource(new Path(path, filename).toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION
                        , "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, URLConnection.guessContentTypeFromName(filename))
                .body(file);
    }

    @PostMapping("/api/v1/files/{path}")
    public ResponseEntity<Result> handleFileUpload(@PathVariable("path") String path
            , MultipartFile file) {
        service.store(file, path);
        FilesQueryResult queryResult = new FilesQueryResult(
                Collections.singletonList(file.getOriginalFilename()));
        return ResponseEntity.ok(queryResult);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<Result> handleStorageFileNotFound(StorageFileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

}
