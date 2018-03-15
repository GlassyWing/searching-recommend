package org.manlier.srapp.services;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.manlier.srapp.config.SolrProperties;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolrSearchingService implements SearchingService {

	private SolrClient solrClient;

	private final SolrProperties properties;

	@Inject
	public SolrSearchingService(SolrClient solrClient, SolrProperties properties) {
		this.solrClient = solrClient;
		this.properties = properties;
	}

	@Override
	public List<String> searchComps(String desc) throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery(desc);
		query.addField("id");
		QueryResponse response = solrClient.query(properties.getCollectionName(), query);
		SolrDocumentList documents = response.getResults();
		return documents.stream().map(doc -> (String) doc.get("id"))
				.collect(Collectors.toList());
	}
}
