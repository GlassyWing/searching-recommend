package org.manlier.srapp.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";

    /**
     * 默认位于hdfs的构件目录
     */
    private String defaultComponentsDir = "input/symbols";

    /**
     * 默认位于hdfs的同义词目录
     */
    private String defaultSynonymsDir = "input/synonyms";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDefaultComponentsDir() {
        return defaultComponentsDir;
    }

    public void setDefaultComponentsDir(String defaultComponentsDir) {
        this.defaultComponentsDir = defaultComponentsDir;
    }

    public String getDefaultSynonymsDir() {
        return defaultSynonymsDir;
    }

    public void setDefaultSynonymsDir(String defaultSynonymsDir) {
        this.defaultSynonymsDir = defaultSynonymsDir;
    }
}
