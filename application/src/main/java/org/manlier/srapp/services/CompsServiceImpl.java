package org.manlier.srapp.services;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.manlier.srapp.config.SolrProperties;
import org.manlier.srapp.dao.CompsDAO;
import org.manlier.srapp.entities.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompsServiceImpl implements CompsService {

    private final SolrClient solrClient;
    private final CompsDAO compsDAO;

    private final SolrProperties properties;

    public CompsServiceImpl(SolrClient solrClient
            , SolrProperties properties
            , CompsDAO compsDAO) {
        this.solrClient = solrClient;
        this.properties = properties;
        this.compsDAO = compsDAO;
    }

    @Override
    public Optional<Component> searchComp(String id) throws IOException {
        return compsDAO.getComponentByName(id);
    }

    @Override
    public List<Component> searchComps(String desc, int rows) throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery(desc);
        query.addField("id");
        query.addField("desc");
        query.setRows(rows);
        QueryResponse response = solrClient.query(properties.getCollectionName(), query);
        SolrDocumentList documents = response.getResults();
        return documents.stream().map(doc -> new Component((String) doc.get("id"), (String) doc.get("desc")))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Component> updateComp(Component component) throws IOException {
        Optional<Component> oldComp = compsDAO.getComponentByName(component.getId());
        if (oldComp.isPresent()) {
            compsDAO.updateComponent(component);
            return oldComp;
        }
        return Optional.empty();
    }

    @Override
    public int deleteComp(String id) throws IOException {
        return compsDAO.deleteComponentByName(id);
    }

    @Override
    public int addComp(Component component) throws IOException {
        return compsDAO.addComponent(component);
    }
}
