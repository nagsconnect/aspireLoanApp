package com.aspire.loanApp.service;

import com.aspire.loanApp.entity.LoanApplication;

public interface LoanApplicationService {
    LoanApplication createLoanApplication(LoanApplication loanApplication);

    LoanApplication getLoanApplication(String applicationId);

    boolean exists(String applicationId);

    LoanApplication submitLoanApplication(String applicationId);
}
