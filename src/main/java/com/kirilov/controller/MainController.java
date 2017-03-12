package com.kirilov.controller;

import com.kirilov.model.Account;
import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Controller
@EnableWebMvc
public class MainController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listAccounts(Model model) {
        List<Account> accounts = null;
        try {
            accounts = transactionService.getAllAccount();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        model.addAttribute("accounts", accounts);
        return "index";
    }
}

