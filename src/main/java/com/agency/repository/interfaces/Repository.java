package com.agency.repository.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * One interface will work for all by using Generics.
 *
 */
public interface Repository<T> {
    void save(T entity);
    //Optional<T> is a container that may or may not contain a value. It forces callers to handle the case where a record is not found - instead of returning null.
    Optional<T> findById(int id);
    List<T> findAll();
    void update(T entity);
    void delete(int id);
    boolean exists(int id);
}
