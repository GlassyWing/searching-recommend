package org.manlier.srapp.services;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SolrSearchingServiceTest {

	@Inject
	private SolrSearchingService service;

	@Test
	public void testInit() {

	}

	@Test
	public void testSearching() throws IOException, SolrServerException {
		List<String> ids = service.searchComps("45");
		System.err.println(ids);
	}

}