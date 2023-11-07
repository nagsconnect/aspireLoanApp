package com.aspire.loanApp.entity;

import java.util.Date;

public class ScheduledPayment {
    public int id;
    public String applicationId;
    public double amount;
    public double paidAmount = 0;
    public Date scheduledDate;
    public PaymentStatus status = PaymentStatus.PENDING;
}
