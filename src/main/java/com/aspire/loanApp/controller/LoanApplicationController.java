package com.aspire.loanApp.controller;

import com.aspire.loanApp.entity.LoanApplication;
import com.aspire.loanApp.entity.LoanStatus;
import com.aspire.loanApp.entity.PaymentStatus;
import com.aspire.loanApp.entity.ScheduledPayment;
import com.aspire.loanApp.entity.response.LoanApplicationDetails;
import com.aspire.loanApp.service.AccountService;
import com.aspire.loanApp.service.LoanApplicationService;
import com.aspire.loanApp.service.PaymentService;
import com.aspire.loanApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/loan-applications")
public class LoanApplicationController {
    private final LoanApplicationService loanApplicationService;
    private final AccountService accountService;
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired
    public LoanApplicationController(LoanApplicationService loanApplicationService, AccountService accountService, UserService userService, PaymentService paymentService) {
        this.loanApplicationService = loanApplicationService;
        this.accountService = accountService;
        this.userService = userService;
        this.paymentService = paymentService;
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

    @PutMapping("/approve")
    public ResponseEntity<?> approveLoanApplication(
            @RequestParam String applicationId, @RequestParam String userId
    ) {
        if (!loanApplicationService.exists(applicationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan Application not found");
        }

        LoanApplication loanApplication = loanApplicationService.getLoanApplication(applicationId);
        if (loanApplication.status != LoanStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request b/c loan can be approved only once and current status is " + loanApplication.status);
        }

        if (!userService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied");
        }

        LoanApplication processedLoanApplication = loanApplicationService.process(applicationId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(processedLoanApplication);
    }

    @GetMapping
    public ResponseEntity<?> getLoanApplication(
            @RequestParam String applicationId
    ) {
        if (!loanApplicationService.exists(applicationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan Application not found");
        }

        LoanApplication loanApplication = loanApplicationService.getLoanApplication(applicationId);
        return ResponseEntity.status(HttpStatus.OK).body(loanApplication);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getLoanApplicationScheduledPayment(
            @RequestParam String applicationId
    ) {
        if (!loanApplicationService.exists(applicationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan Application not found");
        }

        LoanApplicationDetails loanApplicationDetails = new LoanApplicationDetails();
        loanApplicationDetails.scheduledPaymentList = paymentService.getScheduledPayment(applicationId);
        loanApplicationDetails.loanApplication = loanApplicationService.getLoanApplication(applicationId);
        double loanAmount = loanApplicationDetails.loanApplication.amount;
        for (ScheduledPayment scheduledPayment : loanApplicationDetails.scheduledPaymentList) {
            if (scheduledPayment.status == PaymentStatus.PAID) {
                loanApplicationDetails.paidAmount += scheduledPayment.amount;
            }
        }
        loanApplicationDetails.pendingAmount = loanAmount - loanApplicationDetails.paidAmount;
        return ResponseEntity.status(HttpStatus.OK).body(loanApplicationDetails);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllLoanApplicationForUser(
            @RequestParam String userId
    ) {
        if (!userService.userExists(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<LoanApplication> userLoanApplications = loanApplicationService.getLoanApplicationsOfUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userLoanApplications);
    }

    @PutMapping("/close")
    public ResponseEntity<?> closeLoanApplication(
            @RequestParam String applicationId,
            @RequestParam String userId
    ) {
        if (!loanApplicationService.exists(applicationId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan Application not found");
        }

        if (!userService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied");
        }

        LoanApplication loanApplication =  loanApplicationService.closeLoanApplication(applicationId);

        if (loanApplication.status != LoanStatus.CLOSED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Loan Application can't be closed, PaidAmount not tallied with loanProcessedAmount");
        }
        return ResponseEntity.status(HttpStatus.OK).body(loanApplication);
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
