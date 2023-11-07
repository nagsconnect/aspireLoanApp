package com.aspire.loanApp.service;

import com.aspire.loanApp.dao.inmemory.AccountDao;
import com.aspire.loanApp.dao.inmemory.UserDao;
import com.aspire.loanApp.entity.Account;
import com.aspire.loanApp.entity.User;
import com.aspire.loanApp.service.impl.DefaultAccountService;
import com.aspire.loanApp.service.impl.DefaultUserService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DefaultAccountServiceTest {
    private AccountService accountService;
    private UserService userService;

    @BeforeClass
    public void init() {
        AccountDao accountDao = new AccountDao();
        UserDao userDao = new UserDao();
        userService = new DefaultUserService(userDao);
        accountService = new DefaultAccountService(accountDao);
        setupDefaultUsersAndAccounts();
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

    @Test
    public void testAccountValidation_WhenAccountAssociatedWithRightUser() {
        Assert.assertTrue(accountService.accountValidation("1", "test@gmail.com"));
    }

    @Test
    public void testAccountValidation_WhenAccountAssociatedWithWrongUser() {
        Assert.assertTrue(accountService.accountValidation("1", "test@outlook.com"));
    }
}
