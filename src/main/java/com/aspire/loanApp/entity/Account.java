package com.aspire.loanApp.entity;

import lombok.Data;

@Data
public class Account {
    public String accountId;
    public String userId;
    public double balance;
    public AccountStatus status;
}
