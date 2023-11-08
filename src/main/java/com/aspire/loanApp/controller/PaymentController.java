package com.aspire.loanApp.controller;

import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.PaymentStatus;
import com.aspire.loanApp.entity.ScheduledPayment;
import com.aspire.loanApp.service.LoanApplicationService;
import com.aspire.loanApp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/loan-applications/payments")
public class PaymentController {
    private PaymentService paymentService;
    private LoanApplicationService loanApplicationService;

    @Autowired
    public PaymentController(PaymentService paymentService, LoanApplicationService loanApplicationService) {
        this.paymentService = paymentService;
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping
    public ResponseEntity<?> payment(
            @RequestParam String loanApplicationId,
            @RequestParam int term,
            @RequestParam double amount
    ) {
        if (!loanApplicationService.exists(loanApplicationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan Application not found");
        }

        LoanApplication loanApplication = loanApplicationService.getLoanApplication(loanApplicationId);
        if (loanApplication.term < term || loanApplication.term <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Loan scheduled term is incorrect");
        }

        List<ScheduledPayment> scheduledPaymentList = paymentService.getScheduledPayment(loanApplicationId);
        ScheduledPayment prevScheduledPayment = term - 2 >= 0 ? scheduledPaymentList.get(term - 2) : null;
        ScheduledPayment  scheduledPayment = scheduledPaymentList.get(term - 1);
        if (prevScheduledPayment != null && prevScheduledPayment.status == PaymentStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Previous scheduled payment term is pending, connect with customer suppport and clear ");
        }

        if (scheduledPayment.status != PaymentStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This scheduled payment term is not pending, current status is " + scheduledPayment.status);
        }

        if (scheduledPayment.amount > amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("amount must be greater than " + amount);
        }

        List<ScheduledPayment> processedScheduledPaymentList = paymentService.payScheduledPayment(loanApplicationId, term, amount);
        return ResponseEntity.status(HttpStatus.OK).body(processedScheduledPaymentList);
    }
}
