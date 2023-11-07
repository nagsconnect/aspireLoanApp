package com.aspire.loanApp.service.impl;

import com.aspire.loanApp.dao.inmemory.UserDao;
import com.aspire.loanApp.entity.User;
import com.aspire.loanApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DefaultUserService implements UserService {
    private final UserDao userDao;

    @Autowired
    public DefaultUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void addUser(User user) {
        // Set createdAt and updatedAt to the current time
        user.createdAt = new Date();
        user.updatedAt = new Date();

        userDao.create(user.id, user);
    }

    @Override
    public boolean userExists(String userId) {
        return userDao.get(userId).isPresent();
    }
}
