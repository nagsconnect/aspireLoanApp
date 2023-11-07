package com.aspire.loanApp.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    public String id; //id here associated with email of the user
    public String name;
    public Date createdAt;
    public Date updatedAt;
}
