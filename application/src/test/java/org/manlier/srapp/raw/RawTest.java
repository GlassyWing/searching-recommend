package org.manlier.srapp.raw;

import org.apache.hadoop.fs.Path;
import org.junit.Test;

public class RawTest {


    @Test
    public void testPath() {
        Path path = new Path("/comps", "ARRAY.html");
        System.out.println(path);
        System.out.println(path.getName());
    }
}
