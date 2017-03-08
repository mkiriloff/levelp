package com.kirilov.controller;

import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Controller
@EnableWebMvc
public class MainController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listAccounts(Model model) {
        model.addAttribute("accounts", transactionService.getAllAccount());
        return "index";
    }
}

