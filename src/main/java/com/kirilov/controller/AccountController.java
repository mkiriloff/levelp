package com.kirilov.controller;

import com.kirilov.model.Account;
import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
public class AccountController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/add", params = {"firstname", "lastname"}, method = RequestMethod.POST)
    public
    @ResponseBody
    String addAccount(@RequestParam("firstname") String firstName,
                      @RequestParam("lastname") String lastName) {
        Account account = new Account();
        account.setFirstname(firstName);
        account.setLastname(lastName);
        transactionService.addAccount(account);
        return "ok";
    }

    @RequestMapping(value = "/delete", params = {"id"}, method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteAccount(@RequestParam("id") int id) {
        try {
            transactionService.deleteAccount(id);
        } catch (EmptyResultDataAccessException e) {
            return "Account not found";
        }
        return "ok";
    }
}