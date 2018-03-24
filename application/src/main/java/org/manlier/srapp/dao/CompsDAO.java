package org.manlier.srapp.dao;


import org.manlier.srapp.entities.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CompsDAO {

    /**
     * 通过构件名获得构件
     *
     * @param compName 构件名
     * @return Optional对象
     */
    Optional<Component> getComponentByName(String compName) throws IOException;

    /**
     * 添加一个构件
     *
     * @param component 构件对象
     */
    void addComponent(Component component) throws IOException;

    /**
     * 添加多个构件
     *
     * @param components 构件集合
     * @return 添加成功的构件数量
     */
    int addComponents(List<Component> components) throws IOException;

    /**
     * 更新一个构件信息
     *
     * @param component 构件对象
     */
    void updateComponent(Component component) throws IOException;

    /**
     * 更新多个构件
     *
     * @param components 构件集合
     * @return 更新成功的构件数量
     */
    int updateComponents(List<Component> components) throws IOException;

    /**
     * 根据构件名删除一个构件
     *
     * @param compName 构件名
     */
    void deleteComponentByName(String compName) throws IOException;

    /**
     * 根据构件名删除多个构件
     *
     * @param names 构件名
     * @return 删除成功的构件数量
     */
    int deleteComponentsByName(String... names) throws IOException;
}
