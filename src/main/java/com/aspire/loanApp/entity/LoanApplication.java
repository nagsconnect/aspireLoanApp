package com.aspire.loanApp.entity;

import lombok.Data;

@Data
public class LoanApplication {
    public String applicationId;
    public String userId;
    public double amount;
    public int term;
    public double processingFee;
    public LoanStatus status;
}
