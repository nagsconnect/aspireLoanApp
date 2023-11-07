package com.aspire.loanApp.service;

import com.aspire.loanApp.entity.User;

public interface UserService {
    void addUser(User user);
    boolean userExists(String userId);
}
