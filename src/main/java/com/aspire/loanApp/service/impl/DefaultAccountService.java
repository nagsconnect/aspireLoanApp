package com.aspire.loanApp.service.impl;

import com.aspire.loanApp.dao.inmemory.AccountDao;
import com.aspire.loanApp.entity.Account;
import com.aspire.loanApp.entity.AccountStatus;
import com.aspire.loanApp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class DefaultAccountService implements AccountService {
    private final AccountDao accountDao;

    @Autowired
    public DefaultAccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void createAccount(Account account) {
        if (account.accountId == null || account.accountId.isEmpty()) {
            account.accountId = String.valueOf(accountDao.generateAccountNum());
        }
        account.status = AccountStatus.ACTIVE;
        accountDao.create(account.accountId, account);
    }

    @Override
    public boolean accountValidation(String accountId, String userId) {
        Optional<Account> optional = accountDao.get(accountId);
        if (optional.isPresent()) {
            Account account = optional.get();
            return Objects.equals(account.userId, userId);
        }
        return false;
    }
}
