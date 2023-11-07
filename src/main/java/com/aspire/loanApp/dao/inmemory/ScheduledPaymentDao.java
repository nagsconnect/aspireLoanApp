package com.aspire.loanApp.dao.inmemory;

import com.aspire.loanApp.entity.ScheduledPayment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ScheduledPaymentDao {
    private final Map<String, List<ScheduledPayment>> loanToScheduledPayments = new HashMap<>();

    public void create(String loanApplicationId, List<ScheduledPayment> scheduledPaymentList) {
        loanToScheduledPayments.put(loanApplicationId, scheduledPaymentList);
    }

    public Optional<List<ScheduledPayment>> get(String loanApplicationId) {
        return Optional.ofNullable(loanToScheduledPayments.get(loanApplicationId));
    }
}
