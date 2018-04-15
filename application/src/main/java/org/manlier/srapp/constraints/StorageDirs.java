package org.manlier.srapp.constraints;

import java.util.Arrays;

public enum StorageDirs {
    COMPS;

    public static String[] names() {
        return  Arrays.stream(values()).map(Enum::name).toArray(String[]::new);
    }
}
