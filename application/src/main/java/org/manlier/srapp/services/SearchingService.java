package org.manlier.srapp.services;

import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

public interface SearchingService {

	List<String> searchComps(String desc) throws IOException, SolrServerException;
}
