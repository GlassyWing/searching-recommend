package org.manlier.srapp.common;


import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 抽象文件导入器
 *
 * @param <T> 导入的数据类型
 */
public abstract class AbstractFileImporter<T> implements FileImporter {

    private Reader<T> reader = getReader();
    private Writer<T> writer = getWriter();

    protected abstract Reader<T> getReader();

    protected abstract Writer<T> getWriter();

    public void importFromFiles(Stream<Path> paths) {
        writer.write(reader.read(paths));
    }
}
