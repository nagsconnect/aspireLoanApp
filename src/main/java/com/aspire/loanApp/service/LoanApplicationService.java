package com.aspire.loanApp.service;

import com.aspire.loanApp.entity.LoanApplication;

import java.util.List;

public interface LoanApplicationService {
    LoanApplication createLoanApplication(LoanApplication loanApplication);

    LoanApplication getLoanApplication(String applicationId);

    boolean exists(String applicationId);

    LoanApplication submitLoanApplication(String applicationId);

    LoanApplication process(String applicationId, String userId);

    List<LoanApplication> getLoanApplicationsOfUser(String userId);

    LoanApplication closeLoanApplication(String applicationId);
}
