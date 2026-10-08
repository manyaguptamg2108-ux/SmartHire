package dao;

import exception.DatabaseException;
import java.util.List;

/**
 * Generic Data Access Object (DAO) interface defining common CRUD operations.
 *
 * @param <T> Domain entity type
 */
public interface GenericDAO<T> {
    T findById(int id) throws DatabaseException;
    List<T> findAll() throws DatabaseException;
    boolean save(T entity) throws DatabaseException;
    boolean update(T entity) throws DatabaseException;
    boolean delete(int id) throws DatabaseException;
}
