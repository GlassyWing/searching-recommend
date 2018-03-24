package org.manlier.srapp.component;

import org.apache.hadoop.fs.Path;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.manlier.srapp.config.SolrProperties;
import org.manlier.srapp.dao.CompsDAO;
import org.manlier.srapp.entities.Component;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CompsServiceImpl(SolrClient solrClient
            , SolrProperties properties
            , CompsDAO compsDAO
            ) {
        this.solrClient = solrClient;
        this.properties = properties;
        this.compsDAO = compsDAO;
    }

    @Override
    public Optional<Component> searchComp(String id) {
        try {
            return compsDAO.getComponentByName(id);
        } catch (IOException e) {
            throw new ComponentException("Fail to load component: " + id, e);
        }
    }

    @Override
    public List<Component> searchComps(String desc, int rows) {
        SolrQuery query = new SolrQuery(desc);
        query.addField("id");
        query.addField("desc");
        query.setRows(rows);
        QueryResponse response;
        try {
            response = solrClient.query(properties.getCollectionName(), query);
        } catch (SolrServerException | IOException e) {
            throw new ComponentException("Fail to load components.", e);
        }
        SolrDocumentList documents = response.getResults();
        return documents.stream()
                .map(doc -> new Component((String) doc.get("id"), (String) doc.get("desc")))
                .collect(Collectors.toList());
    }

    @Override
    public Component updateComp(Component component) {
        try {
            Optional<Component> oldComp = compsDAO.getComponentByName(component.getId());
            if (oldComp.isPresent()) {
                compsDAO.updateComponent(component);
                return oldComp.get();
            } else {
                throw new ComponentNotFoundException("Fail to update component, because the component does not exist.");
            }
        } catch (IOException e) {
            throw new ComponentException("Fail to update component: " + component.getId(), e);
        }
    }

    @Override
    public void deleteComp(String id) {
        try {
            compsDAO.deleteComponentByName(id);
        } catch (IOException e) {
            throw new ComponentException("Fail to delete component: " + id, e);
        }
    }

    @Override
    public Component addComp(Component component) {
        try {
            Optional<Component> quantum = compsDAO.getComponentByName(component.getId());
            if (quantum.isPresent()) {
                throw new ComponentAlreadyExistsException(String.format("The component: %s already exists, can not be added"
                        , component.getId()));
            }
            compsDAO.addComponent(component);
            return component;
        } catch (IOException e) {
            throw new ComponentException("Fail to add component: " + component.getId(), e);
        }
    }

}
