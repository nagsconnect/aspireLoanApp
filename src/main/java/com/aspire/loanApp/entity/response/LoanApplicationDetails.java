package com.aspire.loanApp.entity.response;

import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.ScheduledPayment;
import lombok.Data;

import java.util.List;

@Data
public class LoanApplicationDetails {
    public LoanApplication loanApplication;
    public List<ScheduledPayment> scheduledPaymentList;
    public double paidAmount;
    public double pendingAmount;
}
