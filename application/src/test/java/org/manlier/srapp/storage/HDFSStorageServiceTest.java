package org.manlier.srapp.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HDFSStorageServiceTest {

    @Named("HDFSStorageService")
    @Inject
    private StorageService service;

    @Test
    public void init() {
        service.init();
    }

    @Test
    public void store() {
    }

    @Test
    public void loadAll() {
        service.loadAll("./").forEach(path ->{

        });
    }

    @Test
    public void load() {
    }

    @Test
    public void loadAsResource() {
    }

    @Test
    public void deleteAll() {
    }
}