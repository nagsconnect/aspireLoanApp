package com.aspire.loanApp.controller;

import com.aspire.loanApp.entity.Account;
import com.aspire.loanApp.entity.User;
import com.aspire.loanApp.service.AccountService;
import com.aspire.loanApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
This particular controller created as part of handling custom user and account creations only
as per assumptions this happens outside loanApp. So this helps user, account creation as part of testing.
 */
@RestController
@RequestMapping("v1/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (!isValidEmailId(user.id)) {
            return new ResponseEntity<>("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        if (user.name == null || user.name.isEmpty()) {
            return new ResponseEntity<>("Name is required", HttpStatus.BAD_REQUEST);
        }
        userService.addUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestBody Account account) {
        if (!isValidUser(account.userId)) {
            return new ResponseEntity<>("Invalid user email id" + account.userId, HttpStatus.BAD_REQUEST);
        }

        if (account.accountId != null) {
            if (!account.accountId.isEmpty()) {
                return new ResponseEntity<>("AccountId must be generated during account creation, sending provide custom account number fails account creation", HttpStatus.BAD_REQUEST);
            }
        }

        if (account.balance < 0) {
            return new ResponseEntity<>("Account balance must be >= 0", HttpStatus.BAD_REQUEST);
        }
        accountService.createAccount(account);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    private boolean isValidUser(String userId) {
        return userService.userExists(userId);
    }

    private boolean isValidEmailId(String emailId) {
        return emailId != null && emailId.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
    }
}
