package com.aspire.loanApp.controller;

import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.LoanStatus;
import com.aspire.loanApp.service.AccountService;
import com.aspire.loanApp.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/loan-applications")
public class LoanApplicationController {
    private final LoanApplicationService loanApplicationService;
    private final AccountService accountService;

    @Autowired
    public LoanApplicationController(LoanApplicationService loanApplicationService, AccountService accountService) {
        this.loanApplicationService = loanApplicationService;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> createLoanApplication(@RequestBody LoanApplication loanApplication) {
        if (!isValidClientRequest(loanApplication)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Client Request, check if account, user exists and account associated with right user, amount > 0, term in weeks > 0");
        }

        LoanApplication createdLoanApplication = loanApplicationService.createLoanApplication(loanApplication);

        return new ResponseEntity<>(createdLoanApplication, HttpStatus.CREATED);
    }

    @PutMapping("/submit")
    public ResponseEntity<?> submitLoanApplication(
            @RequestParam String applicationId) {
        if (!loanApplicationService.exists(applicationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan Application not found");
        }

        LoanApplication loanApplication = loanApplicationService.getLoanApplication(applicationId);
        if (loanApplication.status != LoanStatus.CREATED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Submission b/c loan can be submitted only once and current status is " + loanApplication.status);
        }

        LoanApplication submittedApplication = loanApplicationService.submitLoanApplication(applicationId);
        return ResponseEntity.status(HttpStatus.OK).body(submittedApplication);
    }

    private boolean isValidClientRequest(LoanApplication loanApplication) {
        if (!accountService.accountValidation(loanApplication.accountId, loanApplication.userId)) {
            return false;
        }
        if (loanApplication.amount <= 0) {
            return false;
        }
        return loanApplication.term > 0;
    }
}
