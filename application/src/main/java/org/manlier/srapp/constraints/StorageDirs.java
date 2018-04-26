package org.manlier.srapp.constraints;

import java.util.Arrays;

/**
 * 上传文件的临时目录
 */
public enum StorageDirs {
    COMPS, THESAURUS;

    public static String[] names() {
        return  Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
    }
}
