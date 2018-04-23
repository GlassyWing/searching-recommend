package org.manlier.srapp.common;


import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 文件导入器接口，用于从文件中导入数据
 */
public interface FileImporter {

    interface Reader<T> {
        Stream<T> read(Stream<Path> paths);
    }

    interface Writer<T> {
        void write(Stream<T> components);
    }

    void importFromFiles(Stream<Path> paths);
}
