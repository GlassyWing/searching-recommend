package org.manlier.srapp.dao;


import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.domain.Component;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentDAO {

    /**
     * 通过构件名获得构件
     *
     * @param compName 构件名
     * @return Component对象
     */
    Component getComponentByName(@Param("name") String compName) throws DataAccessException;

    /**
     * 通过构件ID获得构件
     *
     * @param id 构件编号
     * @return Component对象
     */
    Component getComponentById(@Param("id") int id) throws DataAccessException;

    /**
     * 添加一个构件
     *
     * @param component 构件对象
     */
    void addComponent(@Param("component") Component component) throws DataAccessException;


    /**
     * 更新一个构件信息
     *
     * @param component 构件对象
     */
    void updateComponent(@Param("component") Component component) throws DataAccessException;

    /**
     * 根据构件名删除一个构件
     *
     * @param compName 构件名
     */
    void deleteComponentByName(@Param("name") String compName) throws DataAccessException;
}
