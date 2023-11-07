package com.aspire.loanApp.entity;

import java.util.Date;

public class Transaction {
    public String id;
    public String applicationId;
    public String toAccountId;
    public String fromAccountId;
    public double amount;
    public Date createdAt;
}
