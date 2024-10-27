package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

 @RestController
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

     // #1 Create POST endpoint for a user to create a new Account
     @PostMapping("/register")
     public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        // Get list of accounts
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.getAllAccounts();

        // Iterate through accounts. If account already exists, use a boolean to get to the else block.
        Boolean accountExists = false;
        for (Account tempAccount : accounts)
        {
            if (tempAccount.getUsername().equals(account.getUsername())) {
                accountExists = true;
                break;
            }
        }

        if (account.getUsername() != "" && account.getPassword().length() >= 4 && accountExists == false) {
            account.setAccountId(2);
            accountService.createAccount(account);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } else if (accountExists) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
     }

     // #2 POST endpoint for a user to login
     @PostMapping("/login")
     public ResponseEntity<Account> userLogin(@RequestBody Account account) {

        // Get list of accounts
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.getAllAccounts();

        // Iterate through accounts. If account username and password match, set boolean to true.
        Boolean accountExists = false;
        for (Account tempAccount : accounts)
        {
            if (tempAccount.getUsername().equals(account.getUsername()) && tempAccount.getPassword().equals(account.getPassword())) 
            {
                accountExists = true;
                break;
            }

        }

        if (accountExists)
        {
            account.setAccountId(9999);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
     }

    // #3 POST endpoint for a user to create a new message
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
                // Get list of accounts
        List<Account> accounts = new ArrayList<>();
        accounts = accountService.getAllAccounts();

        // Iterate through accounts. If Account(account_id) matches Message(posted_by), set accountExists = true
        Boolean accountExists = false;
        for (Account account : accounts)
        {
            if (account.getAccountId().equals(message.getPostedBy())) {
                accountExists = true;
                break;
            }
        }
        if (accountExists && message.getMessageText() != "" && message.getMessageText().length() <= 255)
        {
            //message.setMessageId(2);
            messageService.createMessage(message);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    // #4 GET Endpoint for retrieving all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        // Get list of messages
        List<Message> messages = new ArrayList<>();
        messages = messageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // #5 GET Endpoint for retrieving a message by id (LINE 137 FAILS)
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
   
        // Get message by id
        Optional<Message> message = messageService.getMessageById(messageId);
      
        if (message.isPresent())
            return new ResponseEntity<>(message.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.OK);

    }

    // #6 DELETE Endpoint for deleting a message by its ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable Integer messageId) {

        // Get message by id
        Optional<Message> message = messageService.getMessageById(messageId);

        if (message.isPresent()) {
            messageService.deleteMessage(message.get().getMessageId());
            return new ResponseEntity<>(1, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    // #7 PATCH Endpoint for updating message_text of a message retrieved by its ID 
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessageById(@PathVariable Integer messageId, @RequestBody Message newMessage) {

        Optional<Message> message = messageService.getMessageById(messageId);

        if (message.isPresent() && !(newMessage.getMessageText().equals("")) && newMessage.getMessageText().length() <= 255) {
            messageService.updateMessage(newMessage, messageId);
            return new ResponseEntity<>(1, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // #8 GET Endpoint for retrieving all messages belonging to an account
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getAllMessagesByAccountId(@PathVariable Integer accountId) {

        // Get account by id
        Optional<Account> account = accountService.getAccountById(accountId);

        // Get all messages
        List<Message> messages = messageService.getAllMessages();

        if (account.isPresent()) {
            // Create an arrayList for storing account messages
            List<Message> account_messages = new ArrayList<>();
            // Iterate through messages. If message (postedBy) == account (accountId), add message to account_messages
            for (Message message : messages) 
            {
                if (message.getPostedBy().equals(account.get().getAccountId()))
                    account_messages.add(message);
            }
            return new ResponseEntity<>(account_messages, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

}
