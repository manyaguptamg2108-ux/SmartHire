package dao;

import exception.DatabaseException;
import model.User;
import model.enums.UserStatus;

public interface UserDAOInterface extends GenericDAO<User> {
    User findByEmail(String email) throws DatabaseException;
    boolean updateStatus(int userId, UserStatus status) throws DatabaseException;
}
