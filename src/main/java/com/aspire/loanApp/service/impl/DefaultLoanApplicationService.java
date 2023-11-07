package com.aspire.loanApp.service.impl;

import com.aspire.loanApp.dao.inmemory.LoanApplicationDao;
import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.LoanStatus;
import com.aspire.loanApp.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class DefaultLoanApplicationService implements LoanApplicationService {
    private final LoanApplicationDao loanApplicationDao;

    @Autowired
    public DefaultLoanApplicationService(LoanApplicationDao loanApplicationDao) {
        this.loanApplicationDao = loanApplicationDao;
    }
    @Override
    public LoanApplication createLoanApplication(LoanApplication loanApplication) {
        StringBuilder applicationIdBuilder = new StringBuilder();
        applicationIdBuilder.append(loanApplication.userId).append(", ");
        applicationIdBuilder.append(loanApplication.accountId).append(", ");
        applicationIdBuilder.append(loanApplicationDao.getLoanApplicationNumber());
        loanApplication.applicationId = applicationIdBuilder.toString();
        loanApplication.status = LoanStatus.CREATED;
        loanApplicationDao.create(applicationIdBuilder.toString(), loanApplication);
        return loanApplication;
    }

    @Override
    public LoanApplication getLoanApplication(String applicationId) {
        return loanApplicationDao.get(applicationId).orElseThrow(() -> new NoSuchElementException("No loan application exists with given applicationId"));
    }

    @Override
    public boolean exists(String applicationId) {
        return loanApplicationDao.get(applicationId).isPresent();
    }

    @Override
    public LoanApplication submitLoanApplication(String applicationId) {
        LoanApplication loanApplication = getLoanApplication(applicationId);
        loanApplication.status = LoanStatus.PENDING;
        return getLoanApplication(applicationId);
    }
}
