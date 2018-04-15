package org.manlier.srapp.component;

import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class AbstractComponentImporter implements ComponentImporter {

    private Reader reader = getReader();
    private Writer writer = getWriter();

    protected abstract Reader getReader();

    protected abstract Writer getWriter();

    public void importComponents(Stream<Path> paths) {
        writer.write(reader.read(paths));
    }
}
