package org.manlier.srapp.component;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.jsoup.Jsoup;
import org.manlier.common.parsers.CompDocumentParser;
import org.manlier.srapp.common.AbstractFileImporter;
import org.manlier.srapp.config.SolrProperties;
import org.manlier.srapp.constraints.EnvVariables;
import org.manlier.srapp.dao.ComponentDAO;
import org.manlier.srapp.domain.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ComponentServiceImpl extends AbstractFileImporter<Component> implements ComponentService {

    private final SolrClient solrClient;
    private final ComponentDAO compsDAO;

    private final SolrProperties properties;

    @Autowired
    public ComponentServiceImpl(SolrClient solrClient
            , SolrProperties properties
            , ComponentDAO compsDAO) {
        this.solrClient = solrClient;
        this.properties = properties;
        this.compsDAO = compsDAO;
    }

    @Override
    public Optional<Component> searchComp(String name) {
        try {
            Component component = compsDAO.getComponentByName(name);
            if (component == null) return Optional.empty();
            return Optional.of(component);
        } catch (DataAccessException e) {
            throw new ComponentException("Fail to load component: " + name, e);
        }
    }

    @Override
    public Optional<Component> searchComp(int id) {
        Component component = compsDAO.getComponentById(id);
        if (component == null) {
            return Optional.empty();
        }
        return Optional.of(component);
    }

    @Override
    public List<Component> searchComps(String desc, int rows) {
        SolrQuery query = new SolrQuery(desc);
        query.addField("ID");
        query.addField("describe");
        query.setRows(rows);
        QueryResponse response;
        try {
            response = solrClient.query(properties.getCollectionName(), query);
        } catch (SolrServerException | IOException e) {
            throw new ComponentException("Fail to load components.", e);
        }
        SolrDocumentList documents = response.getResults();
        return documents.stream()
                .map(doc -> new Component((String) doc.get("ID"), (String) doc.get("describe")))
                .collect(Collectors.toList());
    }

    @Override
    public Component updateComp(Component component) {
        try {
            Component oldComp = compsDAO.getComponentByName(component.getName());
            if (oldComp != null) {
                compsDAO.updateComponent(component);
                return oldComp;
            } else {
                throw new ComponentNotFoundException("Fail to update component, because the component: " + component.getName() + "does not exist.");
            }
        } catch (DataAccessException e) {
            throw new ComponentException("Fail to update component: " + component.getId(), e);
        }
    }

    @Override
    public void deleteComp(String name) {
        try {
            compsDAO.deleteComponentByName(name);
        } catch (DataAccessException e) {
            throw new ComponentException("Fail to delete component: " + name, e);
        }
    }

    @Override
    public Component addComp(Component component) {
        try {
            Component quantum = compsDAO.getComponentByName(component.getName());
            if (quantum != null) {
                throw new ComponentAlreadyExistsException(String.format("The component: %s already exists, can not be added"
                        , component.getName()));
            }
            compsDAO.addComponent(component);
            return component;
        } catch (DataAccessException e) {
            throw new ComponentException("Fail to add component: " + component.getId(), e);
        }
    }

    @Override
    @Transactional
    @Async
    public CompletableFuture<Void> rebuild() {
        int offset = 0, limit = 150;
        while (true) {
            List<Component> page = compsDAO.getPagedComponents(offset, limit);
            if (page.size() > 0) {
                page.parallelStream().forEach(compsDAO::updateComponent);
                offset += limit;
            } else {
                break;
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected Reader<Component> getReader() {
        return paths -> {
            CompDocumentParser parser = new CompDocumentParser();
            return paths.parallel().flatMap(path -> {
                try {
                    if (parser.parse(Jsoup.parse(path.toFile(), EnvVariables.DEFAULT_CHARSET))) {
                        return parser.getRecords().stream().map(record -> new Component(record.name, record.desc));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Stream.empty();
            });
        };
    }

    @Override
    protected Writer<Component> getWriter() {
        return components -> components.forEach(compsDAO::addComponent);
    }

    @Transactional
    @Override
    public void importFromFiles(Stream<Path> paths) {
        super.importFromFiles(paths);
    }
}
