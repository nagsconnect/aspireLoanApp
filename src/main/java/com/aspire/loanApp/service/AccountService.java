package com.aspire.loanApp.service;

import com.aspire.loanApp.entity.Account;

public interface AccountService {
    void createAccount(Account account);
    boolean accountValidation(String accountId, String userId);
}
