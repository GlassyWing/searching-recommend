package org.manlier.srapp.job;

import org.apache.hadoop.mapreduce.Job;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.mapreduce.JobRunner;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class JobExecutionServiceImpl implements JobExecutionService, DisposableBean {

    private static final String APP_DEP_ARCHIVE_NAME = "application-dep.jar";

    private final List<Job> jobs;

    private final JobRunner compsImporter;

    @Inject
    public JobExecutionServiceImpl(List<Job> jobs
            , JobRunner compsImporter) {
        this.jobs = jobs;
        this.compsImporter = compsImporter;
    }

    public void init() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Resource resource = new ClassPathResource("apprepo/" + APP_DEP_ARCHIVE_NAME);
        Path path = Paths.get(tmpDir, APP_DEP_ARCHIVE_NAME);
        Files.copy(resource.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        jobs.parallelStream().forEach(job -> job.setJar(path.toUri().toString()));
    }

    @Override
    public void importComponents(org.apache.hadoop.fs.Path path) throws Exception {
        compsImporter.call();
    }

    @Override
    public void rebuildComponentsIndex(org.apache.hadoop.fs.Path path) throws Exception {

    }


    @Override
    public void destroy() throws Exception {
        String tmpDir = System.getProperty("java.io.tmpDir");
        Path path = Paths.get(tmpDir, APP_DEP_ARCHIVE_NAME);
        Files.delete(path);
    }
}
