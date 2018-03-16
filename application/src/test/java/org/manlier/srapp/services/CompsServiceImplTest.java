package org.manlier.srapp.services;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.entities.Component;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class CompsServiceImplTest {

    @Inject
    private CompsService service;

    @Test
    public void testInit() {

    }

    @Test
    public void searchComps() throws IOException, SolrServerException {
        List<Component> ids = service.searchComps("取消选取", 3);
        System.err.println(ids);
    }
}