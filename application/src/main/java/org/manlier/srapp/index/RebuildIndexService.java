package org.manlier.srapp.index;

import org.manlier.srapp.component.ComponentService;
import org.springframework.data.hadoop.mapreduce.JarRunner;
import org.springframework.data.hadoop.mapreduce.ToolRunner;
import org.springframework.stereotype.Service;

@Service
public class RebuildIndexService {

    private ComponentService service;

    public RebuildIndexService( ComponentService service) {
        this.service = service;
    }

    public void rebuildIndex() {
        try {
            service.rebuild();
        } catch (Exception e) {
            throw new RebuildIndexException("Fail to rebuild index", e);
        }
    }
}
