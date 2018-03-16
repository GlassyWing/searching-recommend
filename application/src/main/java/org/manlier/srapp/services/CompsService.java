package org.manlier.srapp.services;

import org.apache.solr.client.solrj.SolrServerException;
import org.manlier.srapp.entities.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CompsService {

    /**
     * 通过构件id搜索构件
     *
     * @param id 构件id
     * @return 构件
     */
    Optional<Component> searchComp(String id) throws IOException;

    /**
     * 通过构件描述搜索构件
     *
     * @param desc 构件描述
     * @param rows 指定最多返回多少个构件
     * @return 构件
     */
    List<Component> searchComps(String desc, int rows) throws IOException, SolrServerException;

    /**
     * 更新构件
     *
     * @param component 构件
     * @return 原来的组件
     */
    Optional<Component> updateComp(Component component) throws IOException;

    /**
     * 根据构件id删除构件
     *
     * @param id 构件id
     * @return 删除成功的个数
     */
    int deleteComp(String id) throws IOException;

    /**
     * 添加一个构件
     *
     * @param component 构件
     * @return 添加成功的个数
     */
    int addComp(Component component) throws IOException;

}
