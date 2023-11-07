package com.aspire.loanApp.dao.inmemory;

import com.aspire.loanApp.dao.BaseDao;
import com.aspire.loanApp.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;

/*
Updates, deletes shouldn't be supported as transactions are immutable
 */
@Repository
public class TransactionDao {
    private final Map<String, Transaction> transactions = new HashMap<>();

    public void create(String key, Transaction val) {
        transactions.put(key, val);
    }

    // get all transactions related to accountId both credits and debits
    // this can be implemented better by storing transactions at account level
    // actually if this is DB, then WHERE clause helps to get all transactions by indexing on accountId
    public List<Transaction> get(String accountId) {
        List<Transaction> transactionList  = new ArrayList<>();
        for (Transaction transaction : transactions.values()) {
            if (Objects.equals(transaction.toAccountId, accountId) || Objects.equals(transaction.fromAccountId, accountId)) {
                transactionList.add(transaction);
            }
        }
        transactionList.sort(Comparator.comparing(a -> a.createdAt));
        return transactionList;
    }
}
