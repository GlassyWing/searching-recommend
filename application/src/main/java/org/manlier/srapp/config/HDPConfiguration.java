package org.manlier.srapp.config;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class HDPConfiguration {

    @Bean
    public Configuration hbaseConfiguration() {
        return HBaseConfiguration.create();
    }
}
