package org.manlier.srapp.user;

import org.manlier.srapp.domain.User;

import java.util.Optional;

public interface UserService {

    void addUser(User user);

    Optional<User> selectUserById(int id);

    Optional<User> selectUserByUUID(String uuid);

    void removeUserByUUID(String uuid);
}
