package org.manlier.srapp.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface StorageService<T> {

    /**
     * 初始化工作
     *
     * @param args 初始化参数
     */
    void init(String... args);

    /**
     * 将文件存储到指定目录下
     *
     * @param file 文件对象
     * @param dir  目录
     * @return 存储的文件路径
     */
    CompletableFuture<Optional<T>> store(MultipartFile file, String dir);

    /**
     * 将多个文件存储到指定目录下
     *
     * @param files 文件对象
     * @param dir   目录
     * @return 存储的文件路径
     */
    CompletableFuture<Stream<T>> store(MultipartFile[] files, String dir);

    /**
     * 罗列指定目录下的所有文件
     *
     * @param path 指定目录
     * @return 文件集合
     */
    Stream<T> loadAll(String path);

    /**
     * 载入单个文件
     *
     * @param filename 文件名
     * @return 文件对象
     */
    T load(String subDir, String filename);

    /**
     * 以Resource的格式载入文件
     *
     * @param filename 文件名
     * @return Resource对象
     */
    Resource loadAsResource(String subDir, String filename);

    /**
     * 删除指定目录下的所有文件
     *
     * @param deleteDir 是否删除目录
     * @param subDirs   目录
     */
    void deleteAll(boolean deleteDir, String... subDirs);

    /**
     * 删除指定的所有文件
     *
     * @param pathStream 文件流
     */
    void deleteAll(Stream<T> pathStream);

    /**
     * 删除指定目录下的所有文件，包括目录
     *
     * @param subDirs 目录
     */
    default void deleteAll(String... subDirs) {
        deleteAll(true, subDirs);
    }

    /**
     * 删除指定的文件
     *
     * @param path 文件路径
     */
    void delete(T path);

}
