package org.manlier.srapp.raw;

import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

public class RawTest {

    private Pattern pattern = Pattern.compile("\\w+,\\w+\\.\\w+,\\w+\\.\\w+");

    @Test
    public void testPath() {
        Path path = new Path("/comps", "ARRAY.html");
        System.out.println(path);
        System.out.println(path.getName());
    }

    @Test
    public void testSplit() {
        System.out.println(Arrays.toString("我家".split("\\s+")));
    }

    @Test
    public void testMatch() {
        System.out.println("我家".matches("\\s+"));
    }
}
