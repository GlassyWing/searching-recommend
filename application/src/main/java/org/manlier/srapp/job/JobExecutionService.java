package org.manlier.srapp.job;

import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public interface JobExecutionService {


    /**
     * 完成初始化工作
     */
    void init() throws IOException;

    /**
     * 导入构件
     *
     * @param path 构件路径
     */
    void importComponents(Path path) throws Exception;

    /**
     * 重建构件库索引
     *
     * @param path 构件路径
     */
    void rebuildComponentsIndex(Path path) throws Exception;
}
