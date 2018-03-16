package org.manlier.srapp.config;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class HBaseConfiguration {

    @Bean
    public org.apache.hadoop.conf.Configuration configuration() {
        return org.apache.hadoop.hbase.HBaseConfiguration.create();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Connection connection() throws IOException {
        return ConnectionFactory.createConnection(configuration());
    }
}
