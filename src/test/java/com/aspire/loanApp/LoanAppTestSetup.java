package com.aspire.loanApp;

import com.aspire.loanApp.dao.inmemory.*;
import com.aspire.loanApp.entity.*;
import com.aspire.loanApp.service.AccountService;
import com.aspire.loanApp.service.LoanApplicationService;
import com.aspire.loanApp.service.PaymentService;
import com.aspire.loanApp.service.UserService;
import com.aspire.loanApp.service.impl.DefaultAccountService;
import com.aspire.loanApp.service.impl.DefaultLoanApplicationService;
import com.aspire.loanApp.service.impl.DefaultPaymentService;
import com.aspire.loanApp.service.impl.DefaultUserService;

/*
Singleton implementation of loanApp test setup to ensure only one copy of the objects exists during test session.
 */
public class LoanAppTestSetup {

    private static volatile LoanAppTestSetup instance;
    private static final Object mutex = new Object();

    public AccountService accountService;
    public UserService userService;

    public PaymentService paymentService;
    public LoanApplicationService loanApplicationService;

    private LoanAppTestSetup() {
        AccountDao accountDao = new AccountDao();
        UserDao userDao = new UserDao();
        ScheduledPaymentDao scheduledPaymentDao = new ScheduledPaymentDao();
        TransactionDao transactionDao = new TransactionDao();
        LoanApplicationDao loanApplicationDao = new LoanApplicationDao();
        accountService = new DefaultAccountService(accountDao);
        userService = new DefaultUserService(userDao);
        paymentService = new DefaultPaymentService(scheduledPaymentDao, transactionDao, accountDao);
        loanApplicationService = new DefaultLoanApplicationService(loanApplicationDao, paymentService);
        setupDefaultUsersAndAccounts();
        createSampleLoanApplication();
    }

    public static LoanAppTestSetup getInstance() {
        LoanAppTestSetup result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new LoanAppTestSetup();
            }
        }
        return result;
    }

    private void createSampleLoanApplication() {
        LoanApplication sampleLoanApplication = new LoanApplication();
        sampleLoanApplication.term = 4;
        sampleLoanApplication.amount = 500;
        sampleLoanApplication.userId = "test@outlook.com";
        sampleLoanApplication.accountId = String.valueOf(1);
        loanApplicationService.createLoanApplication(sampleLoanApplication);
    }
    private void setupDefaultUsersAndAccounts() {
        //adding two dummy users
        User user1 = new User();
        user1.name = "testName";
        user1.id = "test@gmail.com";
        userService.addUser(user1);
        User user2 = new User();
        user2.name = "testName2";
        user2.id = "test@outlook.com";
        userService.addUser(user2);
        //adding two accounts one per user
        Account account1 = new Account();
        account1.userId = user1.id;
        account1.balance = 50;
        Account account2 = new Account();
        account2.userId = user2.id;
        account2.balance = 10;
        accountService.createAccount(account1);
        accountService.createAccount(account2);
    }

}
