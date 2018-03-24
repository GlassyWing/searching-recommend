package org.manlier.srapp.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

public interface StorageService<T> {

    /**
     * 初始化工作
     *
     * @param subDirs 相对于根目录的子目录
     */
    void init(String... subDirs);

    /**
     * 将文件存储到指定目录下
     *
     * @param file 文件对象
     * @param dir  目录
     */
    void store(MultipartFile file, String dir);

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
    T load(String filename);

    /**
     * 以Resource的格式载入文件
     *
     * @param filename 文件名
     * @return Resource对象
     */
    Resource loadAsResource(String filename);

    /**
     * 删除指定路径下的所有文件
     *
     * @param path 路径
     */
    void deleteAll(String path);

}
