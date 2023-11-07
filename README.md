# aspireLoanApp
Backend APIs implementation for a Loan Application more details in Readme.md section, using in-memory objects as db

## System requirements to Run the app 
* JAVA 17, maven, PostMan, any JVM supported ide(to debug)
* once repo is pulled to local machine, use below command as this assures system is ready
```mvn clean -e install ```
* Use below command to run the application and use postman collection to use apis
```mvn spring-boot:run```

## Alternative, system setup using script
* Alternatively download the file named setupMacOs.sh for getting all dependencies related to run this repo
* Once file is downloaded then follow below command in order:
```
cd {toPathOfSetupMacOsFile}
chmod +x setupMacOs.sh
mvn spring-boot:run
```
## Run the App - Yup ShowTime!!!! <<MUST READ, should help you>>
* Assuming postman is downloaded and import the json file from folder **postmanCollection** of the repo cloned/git
* There are various apis supported with each api explained below as per customer actions.
* Before running any api, ensure the spring boot application is running on localhost
### Follow stepwise for application functionality (you can any order to see how apis handle exceptions/validations)
Each of the step mentioned below has example data that shows required fields associated with each request to succeed.
* First, Run the apis in folder ```prereqToRun``` with example data. This should create users, accounts
* Second, Create a loan application with existing user and account details. (yeah you can try with wrong/no such details but it shouldn't work thats the expectation)
* Next, Submit the loanApplication which is already created (one can't submit loanApplication if already processed/submitted/closed)
* Next, Admin should be able to approve any loanApplication that is in PENDING state given the loanApplicationId
  * This submission does multiple things in background, like once loan application APPROVED
    1. Do schedule payments for recollection of amount, term-wise (here assumption of term is weekly)
    2. Disburse amount as per loanApplication via payment gateway (OH YEAH!! no real integration just assume in code we do call disburse() functionality)
* Next, User must be able to view all loanApplications related to their userId
* Apart from that, user can view detailed view of loanApplication with scheduled payment details, loan application details, paidAmount, pendingAmountToBePaid
* Then, User can pay via payments api for scheduledPayment (assuming scheduledPayment via cron job sends some notification/forces to pay when user opens bank app for each week)
* Finally, you can check how does loanApplication view once payment made, try scenarios like paying extra amount, paying less amount that required in term payment. (Logic behind how they are handled explained below)
## Assumptions:
* Users are already registered and authenticated to use this app
* Users taken loanTerm is in weeks
* Users taken loan can be repaid during scheduled payment date only but can pay higher amount for which the extra amount is adjusted from user last term ordered by paymentDate.
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
1. customer can get all loan application details against userId or get one loan application based on applicationId
2. Customer can view pendingAmount, amountPaid and all payment details at transaction level for a loanApplication

##### APIs involved:
getAllLoanApplicationsForUser -> GET /v1/loan-applications/users?UserId={userId}
getLoanApplicationDetails -> GET /v1/loan-applications/details?applicationId={appId}
getLoanApplication -> GET /v1/loan-applications?applicationId={appId}

Reusing the tables, LoanApplication, Transaction, User to fetch details

#### Customer repayment
* All scheduled payments are weekly here and the scheduled payment triggers a notification via a cron job (that is external to this application)
* Customer can repay the loan during term scheduled with either greater than or equal to scheduled amount
* In case of greater amount, 
  * extraAmount, the last term amount is adjusted with extraAmount
  * In case of last term amount turns negative, the last to second term is adjusted and so on.
  * Whenever extraAmount sufficient to pay term amount then that particular term payment status stands cancelled.
* if there is any extra amount paid, then it is returned to user via payment gateway(assumption), given that extra amount is captured in history of scheduled payments
##### APIs involved:
loanRepayment -> POST /v1/payment?applicationId={appId}&termId={termId}

##### Schema
PaymentStatus -> PAID, PENDING, CANCELLED