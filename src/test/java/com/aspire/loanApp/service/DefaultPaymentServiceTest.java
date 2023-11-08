package com.aspire.loanApp.service;

import com.aspire.loanApp.LoanAppTestSetup;
import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.PaymentStatus;
import com.aspire.loanApp.entity.ScheduledPayment;
import com.aspire.loanApp.entity.Transaction;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DefaultPaymentServiceTest {
    PaymentService paymentService;
    LoanAppTestSetup loanAppTestSetup;

    @BeforeClass
    public void init() {
        loanAppTestSetup = LoanAppTestSetup.getInstance();
        paymentService = loanAppTestSetup.paymentService;
    }

    @Test(
            expectedExceptions = NoSuchElementException.class,
            expectedExceptionsMessageRegExp = "LoanApplication has no scheduled payments either it is not processed or no such loan application exists"
    )
    public void testGetScheduledPayments_whenNoLoanApplication_thenThrowNoSuchElementException() {
        paymentService.getScheduledPayment("testApplicationId");
    }

    @Test
    public void testDisburseAmountForSampleLoanApplicationId() {
        LoanApplication loanApplication = loanAppTestSetup.loanApplicationService.getLoanApplication("1");
        paymentService.disburseAmount(loanApplication);
        Transaction transaction = loanAppTestSetup.transactionDao.get(loanApplication.accountId).get(0);
        // amount disbursed must be equal to loanApplication requested amount
        Assert.assertEquals(transaction.amount, loanApplication.amount);
    }

    @Test
    public void testGenerateScheduledPaymentsForSampleLoanApplication() {
        LoanApplication loanApplication = loanAppTestSetup.loanApplicationService.getLoanApplication("1");
        paymentService.generateScheduledPayments(loanApplication);
        List<ScheduledPayment> actualList = paymentService.getScheduledPayment(loanApplication.applicationId);
        Assert.assertEquals(actualList.size(), 4);
        int termId = 1;
        for (ScheduledPayment scheduledPayment : actualList) {
            Assert.assertEquals(scheduledPayment.amount, 125.0);
            Assert.assertEquals(scheduledPayment.status, PaymentStatus.PENDING);
            Assert.assertEquals(scheduledPayment.id, termId);
            Assert.assertEquals(scheduledPayment.applicationId, loanApplication.applicationId);
            termId++;
        }
    }

    @Test
    public void testPayScheduledPaymentForSampleLoanApplication_afterGeneratingScheduledPayments() {
        LoanApplication loanApplication = loanAppTestSetup.loanApplicationService.getLoanApplication("1");
        paymentService.generateScheduledPayments(loanApplication);
        paymentService.payScheduledPayment(loanApplication.applicationId, 1, 125.0);
        List<ScheduledPayment> actualList = paymentService.getScheduledPayment(loanApplication.applicationId);
        ScheduledPayment scheduledPayment = actualList.get(0);
        Assert.assertEquals(scheduledPayment.amount, 125.0);
        Assert.assertEquals(scheduledPayment.status, PaymentStatus.PAID);
        Assert.assertEquals(scheduledPayment.id, 1);
        Assert.assertEquals(scheduledPayment.applicationId, loanApplication.applicationId);
    }
}
