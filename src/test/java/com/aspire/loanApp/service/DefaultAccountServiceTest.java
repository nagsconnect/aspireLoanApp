package com.aspire.loanApp.service;

import com.aspire.loanApp.LoanAppTestSetup;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DefaultAccountServiceTest {
    private AccountService accountService;

    @BeforeClass
    public void init() {
        LoanAppTestSetup loanAppTestSetup = LoanAppTestSetup.getInstance();
        accountService = loanAppTestSetup.accountService;
    }

    @Test
    public void testAccountValidation_WhenAccountAssociatedWithRightUser() {
        Assert.assertTrue(accountService.accountValidation("1", "test@gmail.com"));
    }

    @Test
    public void testAccountValidation_WhenAccountAssociatedWithWrongUser() {
        Assert.assertFalse(accountService.accountValidation("1", "test@outlook.com"));
    }
}
