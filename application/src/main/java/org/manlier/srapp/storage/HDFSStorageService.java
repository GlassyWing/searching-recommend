package org.manlier.srapp.storage;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class HDFSStorageService implements StorageService<Path> {

    private Path rootLocation;
    private FileSystem fs;

    @Inject
    public HDFSStorageService(StorageProperties properties
            , FileSystem fileSystem) {
        this.rootLocation = new Path(properties.getLocation());
        this.fs = fileSystem;
    }

    @Override
    public void init(String... subDirs) {
        try {
            fs.mkdirs(rootLocation);
            for (String subDir : subDirs) {
                fs.mkdirs(load(subDir));
            }
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file, String dir) {
        if (file == null) {
            throw new StorageException("The file is not specified!");
        }
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
        }
        try {
            IOUtils.copyBytes(file.getInputStream()
                    , fs.create(load(new Path(dir, Objects.requireNonNull(file.getOriginalFilename())).toString()))
                    , 1024, true);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll(String path) {
        try {
            return Arrays.stream(FileUtil.stat2Paths(fs.listStatus(load(path))));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return new Path(rootLocation, filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path path = load(filename);
            System.err.println(path.toUri());
            Resource resource = new InputStreamResource(fs.open(path));
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (IOException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll(String path) {
        Path path1 = new Path(rootLocation, path);
        try {
            fs.delete(path1, true);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + path, e);
        }
    }
}
