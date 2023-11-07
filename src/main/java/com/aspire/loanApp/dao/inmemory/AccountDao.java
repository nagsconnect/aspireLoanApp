package com.aspire.loanApp.dao.inmemory;

import com.aspire.loanApp.dao.BaseDao;
import com.aspire.loanApp.entity.Account;
import com.aspire.loanApp.entity.AccountStatus;
import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.LoanStatus;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountDao implements BaseDao<String, Account> {
    private final Map<String, Account> accounts = new HashMap<>();
    @Override
    public void create(String key, Account val) {
        accounts.put(key, val);
    }

    @Override
    public Optional<Account> get(String key) {
        return Optional.ofNullable(accounts.get(key));
    }

    @Override
    public Optional<Account> update(String key, Account val) {
        return get(key).isPresent() ? Optional.ofNullable(accounts.put(key, val)) : Optional.empty();
    }

    @Override
    public void delete(String key) {
        Optional<Account> optional = get(key);
        if (optional.isPresent()) {
            Account account = optional.get();
            if (account.status == AccountStatus.ACTIVE) {
                account.status = AccountStatus.CLOSED;
            }
        }
    }

    public List<Account> getUserAccounts(String userId) {
        List<Account> accountList = new ArrayList<>();
        for (String accountId : accounts.keySet()) {
            Account account = accounts.get(accountId);
            if (Objects.equals(account.userId, userId)) {
                accountList.add(account);
            }
        }
        return accountList;
    }
}
