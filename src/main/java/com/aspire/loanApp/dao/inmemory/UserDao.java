package com.aspire.loanApp.dao.inmemory;

import com.aspire.loanApp.dao.BaseDao;
import com.aspire.loanApp.entity.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao implements BaseDao<String, User> {
    private final Map<String, User> users = new HashMap<>();
    
    @Override
    public void create(String userId, User user) {
        users.put(userId, user);
    }

    @Override
    public Optional<User> get(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> update(String userId, User user) {
        return get(userId).isPresent() ? Optional.ofNullable(users.put(userId, user)) : Optional.empty();
    }

    /*
        Usually, once deleting user all associated accounts must be closed and all loans cleared.
        For now, user deletion is unsupported.
     */
    @Override
    public void delete(String userId) {
        return;
    }
}
