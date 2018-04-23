package org.manlier.srapp.thesaurus;

import org.manlier.srapp.common.FileImporter;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 同义词典导入器
 */
public interface ThesaurusImporter extends FileImporter {

    void importThesaurus(Stream<Path> pathStream);
}
