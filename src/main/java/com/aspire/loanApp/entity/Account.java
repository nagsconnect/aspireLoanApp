package com.aspire.loanApp.entity;

import lombok.Data;

@Data
public class Account {
    public String accountNum;
    public String userId;
    public double balance;
    public AccountStatus status;
}
