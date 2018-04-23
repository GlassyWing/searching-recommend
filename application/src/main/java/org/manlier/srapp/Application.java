package org.manlier.srapp;

import org.manlier.srapp.config.SolrProperties;
import org.manlier.srapp.constraints.StorageDirs;
import org.manlier.srapp.history.HistoryKafkaProperties;
import org.manlier.srapp.history.HistoryService;
import org.manlier.srapp.index.RebuildIndexService;
import org.manlier.srapp.prediction.PredictionService;
import org.manlier.srapp.storage.StorageProperties;
import org.manlier.srapp.storage.StorageService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("org.manlier.srapp.dao")
@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties({StorageProperties.class
        , SolrProperties.class
        , HistoryKafkaProperties.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(
            StorageService storageService
// ,PredictionService predictionService
// ,HistoryService historyService
//           , RebuildIndexService indexService
    ) {

        return (args -> {
            storageService.deleteAll(".");
            storageService.init(StorageDirs.names());
//            indexService.init();
//            predictionService.init();
//            historyService.init();
        });
    }

    @Bean
    ExitCodeGenerator exitCodeGenerator() {
        return () -> 42;
    }

}
