package org.manlier.srapp.component;

import org.manlier.srapp.domain.Component;

import java.util.List;
import java.util.Optional;

public interface ComponentService extends ComponentImporter {

    /**
     * 通过构件名搜索构件
     *
     * @param name 构件名
     * @return 构件
     */
    Optional<Component> searchComp(String name);

    /**
     * 通过构件id搜索构件
     *
     * @param id 构件id
     * @return 构件
     */
    Optional<Component> searchComp(int id);

    /**
     * 通过构件描述搜索构件
     *
     * @param desc 构件描述
     * @param rows 指定最多返回多少个构件
     * @return 构件
     */
    List<Component> searchComps(String desc, int rows);

    /**
     * 更新构件
     *
     * @param component 构件
     * @return 原来的组件
     */
    Component updateComp(Component component);

    /**
     * 根据构件id删除构件
     *
     * @param id 构件id
     */
    void deleteComp(String id);

    /**
     * 添加一个构件
     *
     * @param component 构件
     * @return 添加成功的个数
     */
    Component addComp(Component component);

}
