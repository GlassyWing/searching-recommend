package org.manlier.srapp.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = String.valueOf(Paths.get(System.getProperty("java.io.tmpdir"), "srapp-upload-dir"));

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
