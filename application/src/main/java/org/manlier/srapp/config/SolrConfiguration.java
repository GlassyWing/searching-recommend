package org.manlier.srapp.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Solr的配置文件
 */
@Configuration
public class SolrConfiguration {

    private SolrProperties solrProperties;

    @Autowired
    public SolrConfiguration(SolrProperties solrProperties) {
        this.solrProperties = solrProperties;
    }

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(solrProperties.getAddress())
                .withConnectionTimeout(solrProperties.getConnectionTimeout())
                .withSocketTimeout(solrProperties.getSocketTimeout())
                .build();
    }
}
