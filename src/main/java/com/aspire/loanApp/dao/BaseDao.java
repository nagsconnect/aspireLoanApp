package com.aspire.loanApp.dao;

import java.util.Optional;

/*
This baseDao covers all basic CRUD operations.
K -> represents unique identifier of the object
V -> represents the object reference
 */
public interface BaseDao<K, V> {
    void create(K key, V val);
    Optional<V> get(K key);
    Optional<V> update(K key, V val);
    void delete(K key);
}
