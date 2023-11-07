package com.aspire.loanApp.service;

import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.ScheduledPayment;

import java.util.List;

public interface PaymentService {

    void generateScheduledPayments(LoanApplication loanApplication);

    void disburseAmount(LoanApplication loanApplication);

    List<ScheduledPayment> getScheduledPayment(String loanApplicationId);

    List<ScheduledPayment> payScheduledPayment(String loanApplicationId, int term, double amount);
}
