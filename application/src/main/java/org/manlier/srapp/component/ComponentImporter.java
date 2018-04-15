package org.manlier.srapp.component;

import org.manlier.srapp.domain.Component;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ComponentImporter {

    interface Reader {
        Stream<Component> read(Stream<Path> paths);
    }

    interface Writer {
        void write(Stream<Component> components);
    }

    void importComponents(Stream<Path> paths);
}
