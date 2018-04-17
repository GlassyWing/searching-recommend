package org.manlier.srapp.dao;

import org.apache.ibatis.annotations.Param;
import org.manlier.srapp.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO {

    void addUser(@Param("user") User user);

    void deleteUserByUUID(@Param("uuid") String uuid);

    User getUserById(@Param("id") int id);

    User getUserByUUID(@Param("uuid") String uuid);
}
