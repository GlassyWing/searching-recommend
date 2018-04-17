package org.manlier.srapp.user;

import org.manlier.srapp.dao.UserDAO;
import org.manlier.srapp.domain.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void addUser(User user) {
        Optional<User> quantum = selectUserByUUID(user.getUuid());
        if (quantum.isPresent()) {
            throw new UserAlreadyExistsException("User: " + user.getUuid() + " already exists.");
        }
        userDAO.addUser(user);
    }

    @Override
    public Optional<User> selectUserById(int id) {
        User user = userDAO.getUserById(id);
        if (user != null) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Optional<User> selectUserByUUID(String uuid) {
        User user = userDAO.getUserByUUID(uuid);
        if (user != null) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public void removeUserByUUID(String uuid) {
        userDAO.deleteUserByUUID(uuid);
    }
}
