package com.aspire.loanApp.service.impl;

import com.aspire.loanApp.dao.inmemory.AccountDao;
import com.aspire.loanApp.dao.inmemory.ScheduledPaymentDao;
import com.aspire.loanApp.dao.inmemory.TransactionDao;
import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.ScheduledPayment;
import com.aspire.loanApp.entity.Transaction;
import com.aspire.loanApp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultPaymentService implements PaymentService {
    private final TransactionDao transactionDao;
    private final ScheduledPaymentDao scheduledPaymentDao;
    private final AccountDao accountDao;

    @Autowired
    public DefaultPaymentService(ScheduledPaymentDao scheduledPaymentDao, TransactionDao transactionDao, AccountDao accountDao) {
        this.scheduledPaymentDao = scheduledPaymentDao;
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
    }

    @Override
    public void generateScheduledPayments(LoanApplication loanApplication) {
        generateScheduledPaymentsWeekly(loanApplication);
    }

    public void generateScheduledPaymentsWeekly(LoanApplication loanApplication) {
        double amountPerTerm = loanApplication.amount / loanApplication.term;
        List<ScheduledPayment> list = new ArrayList<>();
        long SEVEN_DAYS_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;
        Date nextTerm = new Date(new Date().getTime() + SEVEN_DAYS_MILLISECONDS);
        for (int i = 0; i < loanApplication.term; i++) {
            ScheduledPayment scheduledPayment = new ScheduledPayment();
            scheduledPayment.amount = amountPerTerm;
            scheduledPayment.applicationId = loanApplication.applicationId;
            scheduledPayment.id = (i + 1);
            scheduledPayment.scheduledDate = nextTerm;
            long addTime = nextTerm.getTime()+ SEVEN_DAYS_MILLISECONDS;
            nextTerm = new Date(addTime);
            list.add(scheduledPayment);
        }
        scheduledPaymentDao.create(loanApplication.applicationId, list);
    }

    @Override
    public void disburseAmount(LoanApplication loanApplication) {
        // call to payment gateway to do the transaction
        Transaction transaction = new Transaction();
        transaction.id = UUID.randomUUID().toString();
        transaction.amount = loanApplication.amount;
        transaction.fromAccountId = "mainAccount";
        transaction.toAccountId = loanApplication.accountId;
        transaction.createdAt = new Date();
        transactionDao.create(transaction.id, transaction);
        accountDao.get(loanApplication.accountId).orElseThrow(() -> new NoSuchElementException("\"No such account exists\"")).balance += transaction.amount;
        //payment gateway handles actual bank balances this updates on our end to do reconciliation
    }

    @Override
    public List<ScheduledPayment> getScheduledPayment(String loanApplicationId) {
        return scheduledPaymentDao.get(loanApplicationId).orElseThrow(() -> new NoSuchElementException("no loan application found"));
    }
}
