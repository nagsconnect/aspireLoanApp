package com.aspire.loanApp.service;

import com.aspire.loanApp.LoanAppTestSetup;
import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.LoanStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DefaultLoanApplicationServiceTest {
    private LoanApplicationService loanApplicationService;

    @BeforeClass
    public void init() {
        LoanAppTestSetup loanAppTestSetup = LoanAppTestSetup.getInstance();
        loanApplicationService = loanAppTestSetup.loanApplicationService;
    }

    @Test
    public void test_createLoanApplication_thenVerifyIfApplicationCreated() {
        LoanApplication sampleLoanApplication = new LoanApplication();
        sampleLoanApplication.term = 4;
        sampleLoanApplication.amount = 500;
        sampleLoanApplication.userId = "test@gmail.com";
        sampleLoanApplication.accountId = String.valueOf(1);
        loanApplicationService.createLoanApplication(sampleLoanApplication);
        Assert.assertEquals(sampleLoanApplication.status, LoanStatus.CREATED);
        Assert.assertEquals(sampleLoanApplication, loanApplicationService.getLoanApplication("2"));
    }

    @Test
    public void test_submitLoanApplicationMethod_whenSampleApplicationSubmitted_thenVerifySubmission() {
        //test setup creates a loan application with id: 1 hence submit that loan application and validate the loan status pending or not
        Assert.assertEquals(loanApplicationService.getLoanApplication("1").status, LoanStatus.CREATED);
        LoanApplication sampleLoanApplication = loanApplicationService.submitLoanApplication("1");
        Assert.assertEquals(sampleLoanApplication.status, LoanStatus.PENDING);
    }

    @Test
    public void test_loanApplicationExistsMethod_WhenNoSuchApplication_thenReturnFalse() {
        Assert.assertFalse(loanApplicationService.exists("testApplicationId"));
    }

    @Test
    public void test_loanApplicationExistsMethod_WhenApplicationExists_thenReturnTrue() {
        Assert.assertTrue(loanApplicationService.exists("1"));
    }
}
