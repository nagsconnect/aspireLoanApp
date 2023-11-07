# aspireLoanApp
Backend APIs implementation for a Loan Application more details in Readme.md section, using in-memory db

## System requirements to Run the app 
* JAVA 17, maven, PostMan, any JVM supported ide(to debug)
* once repo is pulled to local machine, use below command as this assures system is ready
**mvn clean -e install** 
* Use below command to run the application and use postman collection to use apis
**mvn spring-boot:run** 

## Assumptions:
* Users are already registered and authenticated to use this app
* below schema available with the application,
#### User
id, name, address, createdAt, updatedAt
##### AccountStatus
ACTIVE, CLOSED
#### Account
accountId, balance, userId, accountStatus

## Loan App details
### Brainstorming different user actions to implementation
User Behavior(in words), APIs, entities involved, dbSchema
#### Customer creating a loan application
1. User creates an application with requirement loan amount, loan tenure/term, then loanApplication status set to CREATED
2. Once application is created, Loan application is submitted for verification, then loanApplication status is set to PENDING.

##### APIs involved:
* createLoanApplication -> POST /v1/loan-applications
* submitLoanApplication -> PUT /v1/loan-applications/submit?applicationId={appId}

##### Entities involved:
User, Account, LoanApplication

Table schema required for loan application creation
##### LoanApplicationStatus/Status
CREATED, PENDING, APPROVED, PROCESSED, CLOSED
##### LoanApplication
* applicationId, userId, accountId, loanAmount, loanTerm, processingFee, status

#### Admin approving the loanApplication
1. Admin can get all the applications in pending state
2. Admin can approve a loanApplication against its applicationId, loan status set to Approved.
3. Once loanApplication is approved, it triggers a flow in backend to create scheduledRepayments against applicationId
4. Once loanApplication is approved, Loan amount is disbursed.

##### APIs involved:
ApproveLoanApplication -> PUT /v1/loan-applications/approve?applicationId={appId}
this api internally triggers disburseAmount, and generates scheduledPayments via paymentService.

##### Entities involved:
User, Account, Transaction, LoanApplication, ScheduledPayment

Table schema required for loan application approval.

Reusing Status, LoanApplication schema as prerequisite b/c to approve a loan it must be requested from customer end.
LoanApp status set to approved.
transaction to disburse amount from main account to customer account
update balance of main account and customer account
**To note here balance update actual happen with payment gateways but here these are for reconciliation purposes**
##### LoanDisbursement details as part of Transaction
* Once loan is approved, then amount is disbursed and status set to PROCESSED
* transactionId, applicationId, toAccountId, fromAccountId, amount, createdAt, transactionStatus
##### ScheduledPayment details as part of ScheduledPayment
* Once approved, here multiple rows created. Each row relates to the term details
* id, scheduledDate, applicationId, amount
* each row in the scheduled payment identified by loanApplicationId and termId


#### Customer can view all loans belongs against his userId
1. customer can get all loan application details against userId
2. Customer can view pendingAmount, amountPaid and all payment details at transaction level for a loanApplication

##### APIs involved:
getLoanApplicationsForUser -> GET /v1/loan-applications/{userId}
getLoanApplicationDetails -> GET /v1/loan-applications/{appId}

Reusing the tables, LoanApplication, Transaction, User to fetch details 
##### LoanRepaymentTransaction
* this has all repayment transactions related to the loan application id
* id, transactionId, applicationId

#### Customer repayment 
* Customer can repay the loan during term scheduled with either greater than or equal to scheduled amount
* In case of greater amount, 
  * extraAmount, the last term amount is adjusted with extraAmount
  * In case of last term amount turns negative, the last to second term is adjusted and so on.

##### APIs involved:
loanRepayment -> POST /v1/payment/{appId}/{scheduledId}