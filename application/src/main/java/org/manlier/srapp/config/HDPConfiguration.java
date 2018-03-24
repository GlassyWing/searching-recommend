package org.manlier.srapp.config;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.manlier.srapp.mapreduce.HBaseCompsDirectImporter;
import org.manlier.srapp.mapreduce.WholeFileInputFormat;
import org.manlier.srapp.storage.StorageProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.data.hadoop.mapreduce.JobRunner;

import javax.inject.Singleton;
import java.io.IOException;

/**
 * Hadoop 的配置文件
 */
@Configuration
public class HDPConfiguration {

    private StorageProperties storageProperties;

    public HDPConfiguration(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
        return org.apache.hadoop.hbase.HBaseConfiguration.create(config);
    }

    @Bean
    public FileSystem fileSystem() throws IOException {
        return FileSystem.get(configuration());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Connection connection() throws IOException {
        return ConnectionFactory.createConnection(configuration());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Job compsImportJob() throws IOException {
        Job job = Job.getInstance(configuration());
        FileInputFormat.addInputPath(job, new Path(storageProperties.getDefaultComponentsDir()));
        job.setInputFormatClass(WholeFileInputFormat.class);
        job.setMapperClass(HBaseCompsDirectImporter.HBaseCompsMapper.class);
        job.setNumReduceTasks(0);
        job.setOutputFormatClass(NullOutputFormat.class);
        return job;
    }

    @Bean
    @Singleton
    public JobRunner jobRunner() throws IOException {
        JobRunner jobRunner = new JobRunner();
        jobRunner.setJob(compsImportJob());
        return jobRunner;
    }

}
