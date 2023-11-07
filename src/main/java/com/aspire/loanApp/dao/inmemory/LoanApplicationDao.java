package com.aspire.loanApp.dao.inmemory;

import com.aspire.loanApp.dao.BaseDao;
import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.LoanStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoanApplicationDao implements BaseDao<String, LoanApplication> {
    private final Map<String, LoanApplication> loanApplications = new HashMap<>();
    @Override
    public void create(String applicationId, LoanApplication loanApplication) {
        loanApplications.put(applicationId, loanApplication);
    }

    @Override
    public Optional<LoanApplication> get(String applicationId) {
        return Optional.ofNullable(loanApplications.get(applicationId));
    }

    @Override
    public Optional<LoanApplication> update(String applicationId, LoanApplication loanApplication) {
        return get(applicationId).isPresent() ? Optional.ofNullable(loanApplications.put(applicationId, loanApplication)) : Optional.empty();
    }

    /*
        LoanApplications can't be deleted once created, but can be cancelled after created but not after submitting to verification.
     */
    @Override
    public void delete(String applicationId) {
        Optional<LoanApplication> optional = get(applicationId);
        if (optional.isPresent()) {
            LoanApplication loanApplication = optional.get();
            if (loanApplication.status == LoanStatus.CREATED) {
                loanApplication.status = LoanStatus.CANCELLED;
            }
        }
    }
}
