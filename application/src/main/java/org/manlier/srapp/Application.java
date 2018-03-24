package org.manlier.srapp;

import org.manlier.srapp.config.SolrProperties;
import org.manlier.srapp.job.JobExecutionService;
import org.manlier.srapp.storage.StorageProperties;
import org.manlier.srapp.storage.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class, SolrProperties.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(
            @Qualifier("HDFSStorageService") StorageService storageService
            , JobExecutionService jobExecutionService) {

        return (args -> {
            jobExecutionService.init();
            storageService.deleteAll(".");
            storageService.init("comps");
        });
    }

    @Bean
    ExitCodeGenerator exitCodeGenerator() {
        return () -> 42;
    }

}
