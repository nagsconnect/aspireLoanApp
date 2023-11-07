package com.aspire.loanApp.entity;

import java.util.Date;

public class ScheduledPayment {
    public int id;
    public String applicationId;
    public double amount;
    public Date scheduledDate;
    public PaymentStatus status = PaymentStatus.PENDING;
}
